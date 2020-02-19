package com.webfleet.oauth.controller;

import com.nimbusds.jwt.SignedJWT;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.service.TokenStoreService;
import com.webfleet.oauth.service.feign.Authserver;
import com.webfleet.oauth.service.feign.ResponsePayload;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static com.webfleet.oauth.common.KnownUrls.View.CONSUME;

@Controller
@RequestMapping(KnownUrls.CONSUME)
public class ConsumeController
{
    private static final Logger LOG = LoggerFactory.getLogger(ConsumeController.class);
    private final TokenStoreService tokenStoreService;
    private final Authserver authserver;
    private final String clientId;
    private final String clientSecret;

    public ConsumeController(final Authserver authserver,
                             final TokenStoreService tokenStoreService,
                             @Value("${webfleet.clientid}") final String clientId,
                             @Value("${webfleet.clientsecret}") final String clientSecret)
    {
        this.authserver = authserver;
        this.tokenStoreService = tokenStoreService;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @RequestMapping
    public String consume(Model model, Principal principal)
    {

        // Use refresh_token flow to obtain a new access token
        final String refreshToken = tokenStoreService.getRefreshToken(principal.getName());
        if (refreshToken == null)
        {
            return "redirect:" + KnownUrls.LINK_ACCOUNT;
        }
        // Obtain an access token using stored refresh token
        try
        {
            Map<String, String> params = new HashMap<>();
            // oauth authorization flow we are following, we want to request a token using an auth code flow,
            // thus providing the previously obtained code
            params.put("grant_type", "refresh_token");
            params.put("client_id", clientId); // oauth client identifier
            params.put("client_secret", clientSecret); // oauth client secret matching previous identifier
            params.put("refresh_token", refreshToken); // refresh token for authorizing access token issuing
            final ResponsePayload responsePayload = this.authserver.token(params);
            // At this point we received a new access_token and refresh_token
            tokenStoreService.updateRefreshToken(responsePayload.getRefreshToken(), principal.getName());
            SignedJWT signedJWT = SignedJWT.parse(responsePayload.getValue());
            // TODO add API call to WFS API
            model.addAttribute("result", "");
            return CONSUME.viewName();

        } catch (FeignException e)
        {

            if (HttpStatus.BAD_REQUEST.value() == e.status())
            {
                // Refresh token is no longer valid, either expired or grant was revoked
                tokenStoreService.deleteRefreshToken(principal.getName());
            }
        } catch (ParseException e)
        {
            LOG.error("Couldn't parse JWT token", e);
        }
        return "redirect:" + KnownUrls.ERROR;
    }
}
