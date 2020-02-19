package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.Constants;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.common.RandomKey;
import com.webfleet.oauth.service.TokenStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.security.Principal;

@Controller
@RequestMapping(KnownUrls.LINK_ACCOUNT)
public class LinkAccountController
{
    private static final Logger LOG = LoggerFactory.getLogger(LinkAccountController.class);
    private final TokenStoreService tokenStoreService;
    private final String authserverUrl;
    private final String clientId;
    private final String scopes;
    private String redirectUri;

    public LinkAccountController(final TokenStoreService tokenStoreService,
                                 @Value("${webfleet.authserver}") final String authserverUrl,
                                 @Value("${webfleet.clientid}") final String clientId,
                                 @Value("${webfleet.scopes}") final String scopes,
                                 @Value("${webfleet.redirecturi}") final String redirectUri)
    {
        this.tokenStoreService = tokenStoreService;
        this.authserverUrl = authserverUrl;
        this.clientId = clientId;
        this.scopes = scopes;
        this.redirectUri = redirectUri;
    }


    @RequestMapping
    public String linkAccount(
            @SessionAttribute("random") RandomKey random, // Used it for later verification
            Principal principal
    )
    {
        final String username = principal.getName();
        String refreshToken = tokenStoreService.getRefreshToken(username);
        StringBuilder builder = new StringBuilder("redirect:");
        // Should check if the user's token is already stored and skip this flow if refresh_token is still valid
        // if token is still valid it's unnecessary to request user to authorize us again
        if (refreshToken != null)
        {
            return builder.append(KnownUrls.CONSENT).toString();
        }
        // If we don't have a token yet ask the user to authenticate and authorize us
        builder.append(authserverUrl).append("/oauth/authorize?")
                .append("scope=").append(scopes) // scope we are requesting authorization for this oauth client
                .append("&redirect_uri=").append(redirectUri) // callback url where code from authserver is received
                .append("&client_id=").append(clientId) // our 3rdparty client
                .append("&response_type=code") // authorization flow (authorization code flow)
                .append("&state=").append(random.getKey()); // verification code that will be returned by authserver in the callback
        return builder.toString();
    }
}
