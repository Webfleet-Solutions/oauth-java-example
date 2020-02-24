package com.webfleet.oauth.service.feign;

import com.webfleet.oauth.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;


@FeignClient("authserver")
public interface Authserver {

    @RequestMapping(method = RequestMethod.POST,
            path = Constants.OAUTH_TOKEN_URL,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    OAuthToken token(Map<String, ?> params);

    @RequestMapping(method = RequestMethod.GET,
            path = Constants.REDIRECT_URI_URL)
    void authorize(@PathVariable("random") String random,
                   @PathVariable("clientid") String clientId,
                   @PathVariable("scopes") String scopes);

    @RequestMapping(method = RequestMethod.POST,
            path = Constants.OAUTH_REVOKE_URL,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void revoke(@RequestHeader("Authorization") String token, Map<String, ?> params);

    @RequestMapping(method = RequestMethod.GET,
            path = Constants.ME_URL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Me me(@RequestHeader("Authorization") String token);

}
