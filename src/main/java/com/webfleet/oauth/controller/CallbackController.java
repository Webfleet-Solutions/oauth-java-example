package com.webfleet.oauth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.common.RandomKey;
import com.webfleet.oauth.service.feign.Authserver;
import com.webfleet.oauth.service.feign.ResponsePayload;
import com.webfleet.oauth.service.TokenStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.webfleet.oauth.common.Constants.RESPONSE_PAYLOAD;

/**
 * Note that according to the spec. this endpoint should only be available over a secure channel (e.g. HTTPS)
 * @see  {@link https://tools.ietf.org/html/rfc6749#section-10.5}
 */
@Controller
@RequestMapping(KnownUrls.CALLBACK)
public class CallbackController
{
    private static final Logger LOG = LoggerFactory.getLogger(CallbackController.class);
    private final TokenStoreService tokenStoreService;
    private final String clientId;
    private final String clientSecret;
    private String redirectUri;
    private final Authserver authserver;
    private final ObjectMapper objectMapper;

    public CallbackController(final Authserver authserver,
                              final ObjectMapper objectMapper,
                              final TokenStoreService tokenStoreService,
                              @Value("${webfleet.clientid}") final String clientId,
                              @Value("${webfleet.clientsecret}") final String clientSecret,
                              @Value("${webfleet.redirecturi}") final String redirectUri)
    {
        this.authserver = authserver;
        this.objectMapper = objectMapper;
        this.tokenStoreService = tokenStoreService;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    @RequestMapping
    public String callback(Model model,
                           Principal principal,
                           @RequestParam("code") String code,
                           @RequestParam("state") String state,
                           @SessionAttribute("random") RandomKey randomKey
    ) throws JsonProcessingException
    {

        LOG.info("State and random string: {}, {}", state, randomKey.getKey());
        // state and random parameters should be the same, this may be used by an integrator to verify the request hasn't been tampered
        if (!randomKey.getKey().equals(state))
        {
            throw new IllegalStateException("Someone might have stolen user's grant");
        }
        Map<String, String> params = new HashMap<>();
        // oauth authorization flow we are following, we want to request a token using an auth code flow,
        // thus providing the previously obtained code
        params.put("grant_type", "authorization_code");
        // oauth client identifier, should be registered in TTTSP side
        params.put("client_id", clientId);
        // oauth client secret matching previous identifier
        params.put("client_secret", clientSecret);
        // the code we received and we need to provide as indicated with grant_type parameter
        params.put("code", code);
        // MUST be the same as used to initiate the flow, see https://tools.ietf.org/html/rfc6749#section-10.6
        params.put("redirect_uri", redirectUri);
        // Obtain token and store it somewhere safely
        // This can be executed in a background thread without blocking user's workflow since we dont' require
        // further interaction to access APIs on users' behalf
        final ResponsePayload responsePayload = this.authserver.token(params);
        tokenStoreService.saveRefreshToken(responsePayload.getRefreshToken(), principal.getName());

        // Show the token in the UI for demo purposes
        model.addAttribute(RESPONSE_PAYLOAD, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responsePayload));
        return KnownUrls.View.CALLBACK.viewName();
    }
}
