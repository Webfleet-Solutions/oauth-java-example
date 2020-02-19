package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.service.feign.Authserver;
import com.webfleet.oauth.service.feign.ResponsePayload;
import com.webfleet.oauth.service.TokenStoreService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(KnownUrls.REFRESH)
public class RefreshTokenController
{
    private static final Logger LOG = LoggerFactory.getLogger(RefreshTokenController.class);
    private final TokenStoreService tokenStoreService;
    private final String clientSecret;
    private final String clientId;
    private final Authserver authserver;

    public RefreshTokenController(final Authserver authserver,
                                  final TokenStoreService tokenStoreService,
                                  @Value("${webfleet.clientid}") final String clientId,
                                  @Value("${webfleet.clientsecret}") final String clientSecret)
    {
        this.tokenStoreService = tokenStoreService;
        this.authserver = authserver;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @RequestMapping
    public String refreshToken(Principal principal)
    {
        try
        {
            // Use refresh_token flow to obtain a new access token
            final String refreshToken = tokenStoreService.getRefreshToken(principal.getName());
            if (refreshToken == null)
            {
                LOG.warn("Cannot find a refresh token");
                return "redirect:" + KnownUrls.LINK_ACCOUNT;
            }
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
            return "redirect:" + KnownUrls.CONSUME;
        } catch (FeignException e)
        {
            //Cannot obtain new access token
            if (HttpStatus.BAD_REQUEST.value() == e.status())
            {
                LOG.info("Refresh token is no longer valid, either expired or grant was revoked. Deleting from storage");
                tokenStoreService.deleteRefreshToken(principal.getName());
                // Refresh token expired or was revoked
                return KnownUrls.View.REFRESH.viewName();
            }
            // Allow error handler or other logic to handle the error
            throw e;
        }
    }

}
