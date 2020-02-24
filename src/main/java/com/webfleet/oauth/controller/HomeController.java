package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.Constants;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.service.TokenStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping(KnownUrls.HOME)
public class HomeController extends AbstractMenuController {
    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);
    private final TokenStoreService tokenStoreService;

    public HomeController(final TokenStoreService tokenStoreService) {
        super(KnownUrls.HOME, KnownUrls.SERVICE);
        this.tokenStoreService = tokenStoreService;
    }

    @RequestMapping
    public String home(Model model, Principal principal) {
        final boolean hasRefreshToken = tokenStoreService.hasRefreshToken(principal.getName());
        if (hasRefreshToken) {
            addMenuOption(KnownUrls.CONSUME);
        }
        addMenu(model);
        model.addAttribute(Constants.HAS_REFRESH_TOKEN, hasRefreshToken);
        return KnownUrls.View.HOME.viewName();
    }
}
