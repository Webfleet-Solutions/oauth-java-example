package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.common.RandomKey;
import com.webfleet.oauth.service.TokenStoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.security.Principal;

import static com.webfleet.oauth.common.Constants.HAS_REFRESH_TOKEN;

@Controller
@RequestMapping(KnownUrls.SERVICE)
public class ServiceController extends AbstractMenuController {
    private final TokenStoreService tokenStoreService;
    private final String authserverUrl;
    private final String clientId;
    private final String scopes;
    private final String redirectUri;

    public ServiceController(final TokenStoreService tokenStoreService,
                             @Value("${webfleet.authserver}") final String authserverUrl,
                             @Value("${webfleet.clientid}") final String clientId,
                             @Value("${webfleet.scopes}") final String scopes,
                             @Value("${webfleet.redirecturi}") final String redirectUri) {
        super(KnownUrls.HOME, KnownUrls.CONSUME);
        this.tokenStoreService = tokenStoreService;
        this.authserverUrl = authserverUrl;
        this.clientId = clientId;
        this.scopes = scopes;
        this.redirectUri = redirectUri;
    }

    @RequestMapping
    public String service(
            Model model,
            @SessionAttribute("random") RandomKey random, // Used it for later verification
            Principal principal) {
        final String refreshToken = tokenStoreService.getRefreshToken(principal.getName());
        model.addAttribute(HAS_REFRESH_TOKEN, refreshToken != null);
        model.addAttribute("refresh_token", truncate(refreshToken));
        model.addAttribute("authorizeUrl", buildRedirectString(principal.getName(), random));
        addMenu(model);
        return KnownUrls.View.SERVICE.viewName();
    }

    private String truncate(final String refreshToken) {
        return refreshToken != null && refreshToken.length() > 10 ? refreshToken.substring(0, 10) + "..." : "";
    }

    private String buildRedirectString(final String username, final RandomKey randomKey) {
        StringBuilder builder = new StringBuilder();
        // If we don't have a token yet ask the user to authenticate and authorize us
        builder.append(authserverUrl).append("/oauth/authorize?")
                .append("scope=").append(scopes) // scope we are requesting authorization for this oauth client
                .append("&redirect_uri=").append(redirectUri) // callback url where code from authserver is received
                .append("&client_id=").append(clientId) // our 3rdparty client
                .append("&response_type=code") // authorization flow (authorization code flow)
                .append("&state=").append(randomKey.getKey()); // verification code that will be returned by authserver in the callback
        return builder.toString();
    }
}
