package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.service.TokenStoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static com.webfleet.oauth.common.Constants.HAS_REFRESH_TOKEN;

@Controller
@RequestMapping(value = KnownUrls.CONSENT)
public class ConsentController
{
    private final TokenStoreService tokenStoreService;

    public ConsentController(final TokenStoreService tokenStoreService)
    {
        this.tokenStoreService = tokenStoreService;
    }

    @RequestMapping
    public String consent(Model model, Principal principal)
    {
        final String username = principal.getName();
        // Dummy page demoing we have a token
        model.addAttribute(HAS_REFRESH_TOKEN, tokenStoreService.hasRefreshToken(username));
        return KnownUrls.View.CONSENT.viewName();
    }
}
