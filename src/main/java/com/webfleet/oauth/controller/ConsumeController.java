package com.webfleet.oauth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import com.webfleet.oauth.common.Constants;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.service.TokenStoreService;
import com.webfleet.oauth.service.feign.Authserver;
import com.webfleet.oauth.service.feign.Me;
import com.webfleet.oauth.service.feign.OAuthToken;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.text.ParseException;
import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.webfleet.oauth.common.KnownUrls.View.CONSUME;

@Controller
@RequestMapping(KnownUrls.CONSUME)
public class ConsumeController extends AbstractMenuController {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumeController.class);
    private final TokenStoreService tokenStoreService;
    private final Authserver authserver;
    private final String clientId;
    private final String clientSecret;
    private final ObjectMapper objectMapper;

    public ConsumeController(final Authserver authserver,
                             final TokenStoreService tokenStoreService,
                             final ObjectMapper objectMapper,
                             @Value("${webfleet.clientid}") final String clientId,
                             @Value("${webfleet.clientsecret}") final String clientSecret) {
        super(KnownUrls.HOME, KnownUrls.SERVICE, KnownUrls.REVOKE);
        this.authserver = authserver;
        this.tokenStoreService = tokenStoreService;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.objectMapper = objectMapper;
    }

    @RequestMapping
    public String consume(Model model, Principal principal, HttpSession session) throws JsonProcessingException, ParseException {


        try {
            final String oAuthAccessToken;
            final String refreshToken = tokenStoreService.getRefreshToken(principal.getName());
            // Check if the account was linked previously and we have an access token in the session
            if (session.getAttribute(Constants.OAUTH_ACCESS_TOKEN_SESSION_ATTRIBUTE) == null) {
                if (refreshToken != null) {
                    // if we have a valid refresh token but no access token in the session use refresh token
                    //to retrieve a new access token
                    oAuthAccessToken = renewAccessToken(principal, refreshToken, session);

                } else {
                    return "redirect:" + KnownUrls.SERVICE;
                }
            } else {
                oAuthAccessToken = (String) session.getAttribute(Constants.OAUTH_ACCESS_TOKEN_SESSION_ATTRIBUTE);
            }

            final SignedJWT signedJWT = SignedJWT.parse(oAuthAccessToken);
            final String oAuthTokenValue;
            // Check if the session's access token expired and renew it if it did
            if (!(new Date(Clock.systemUTC().millis())).before(signedJWT.getJWTClaimsSet().getExpirationTime())) {
                // Retrieve refresh token from storage
                // Renew access token with our refresh token
                oAuthTokenValue = renewAccessToken(principal, refreshToken, session);
            } else {
                oAuthTokenValue = oAuthAccessToken;
            }
            // Call WFS API. Please note that "profile-read" scope is required to request "me" resource
            final Me payload = authserver.me("Bearer " + oAuthTokenValue);
            model.addAttribute("result", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
            model.addAttribute("access_token", truncate(oAuthTokenValue));
            addMenu(model);
            return CONSUME.viewName();

        } catch (FeignException e) {
            // A 400 BAD REQUEST error status means refresh token is no longer valid, either expired or was revoked
            if (HttpStatus.BAD_REQUEST.value() == e.status()) {
                tokenStoreService.deleteRefreshToken(principal.getName());
            }
            throw e;
        }
    }

    private String renewAccessToken(final Principal principal, final String refreshToken, final HttpSession session) {
        LOG.info("Renewing access token using refresh token");
        final Map<String, String> params = new HashMap<>();
        // oauth authorization flow we are following, we already authenticated and obtained a refresh token
        // use it to get a new access token
        params.put("grant_type", "refresh_token");
        params.put("client_id", clientId); // oauth client identifier
        params.put("client_secret", clientSecret); // oauth client secret matching previous identifier
        params.put("refresh_token", refreshToken); // refresh token for authorizing access token issuing
        final OAuthToken oAuthToken = this.authserver.token(params);
        // At this point we received a new access_token and refresh_token so we update them in our storages
        tokenStoreService.updateRefreshToken(oAuthToken.getRefreshToken(), principal.getName());
        session.setAttribute(Constants.OAUTH_ACCESS_TOKEN_SESSION_ATTRIBUTE, oAuthToken.getValue());
        return oAuthToken.getValue();
    }

    private String truncate(final String value) {
        return value != null && value.length() > 10 ? value.substring(0, 10) + "..." : "";
    }
}
