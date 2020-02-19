package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.service.feign.Authserver;
import com.webfleet.oauth.service.TokenStoreService;
import feign.FeignException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
@RequestMapping(KnownUrls.REVOKE)
public class RevokeController
{
    private final TokenStoreService tokenStoreService;
    private final Authserver authserver;

    public RevokeController(final TokenStoreService tokenStoreService, final Authserver authserver)
    {
        this.tokenStoreService = tokenStoreService;
        this.authserver = authserver;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String revoke(Principal principal)
    {
        // TODO fill revoke parameters
        try
        {
            authserver.revoke(null);
            // If server revocation succeeded we can safely remove it from our backend
            // as it is no longer valid
            tokenStoreService.deleteRefreshToken(principal.getName());
        }
        catch (FeignException.FeignClientException e)
        {
            return "redirect:" + KnownUrls.ERROR;
        }
        return "redirect:" + KnownUrls.HOME;
    }
}
