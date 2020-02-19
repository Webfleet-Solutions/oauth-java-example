package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.service.TokenStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static com.webfleet.oauth.common.Constants.HAS_REFRESH_TOKEN;

@Controller
@RequestMapping(KnownUrls.HOME)
public class HomeController
{
    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);
    private final TokenStoreService tokenStoreService;

    public HomeController(final TokenStoreService tokenStoreService)
    {
        this.tokenStoreService = tokenStoreService;
    }

    @RequestMapping
    public String home(Model model, Principal principal)
    {
        model.addAttribute(HAS_REFRESH_TOKEN, tokenStoreService.hasRefreshToken(principal.getName()));
        return KnownUrls.View.HOME.viewName();
    }
}
