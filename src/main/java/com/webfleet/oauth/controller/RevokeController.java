package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.Constants;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.service.TokenStoreService;
import com.webfleet.oauth.service.feign.Authserver;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(KnownUrls.REVOKE)
public class RevokeController {
    private static final Logger LOG = LoggerFactory.getLogger(RevokeController.class);
    private final TokenStoreService tokenStoreService;
    private final Authserver authserver;
    private final String clientId;
    private final String clientSecret;

    public RevokeController(final TokenStoreService tokenStoreService,
                            final Authserver authserver,
                            @Value("${webfleet.clientid}") final String clientId,
                            @Value("${webfleet.clientsecret}") final String clientSecret) {
        this.tokenStoreService = tokenStoreService;
        this.authserver = authserver;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String revoke(Principal principal, HttpSession session) {
        try {
            final String base64enc = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
            Map<String, String> params = new HashMap<>();
            params.put("token", tokenStoreService.getRefreshToken(principal.getName()));
            authserver.revoke("Basic " + base64enc, params);
            // If server revocation succeeded we can safely remove it from our backend
            // as it is no longer valid
            tokenStoreService.deleteRefreshToken(principal.getName());
            session.removeAttribute(Constants.OAUTH_ACCESS_TOKEN_SESSION_ATTRIBUTE);
        } catch (FeignException.FeignClientException e) {
            LOG.error("Couldn't revoke refresh token", e);
            return "redirect:" + KnownUrls.ERROR;
        }

        return "redirect:" + KnownUrls.HOME;
    }
}
