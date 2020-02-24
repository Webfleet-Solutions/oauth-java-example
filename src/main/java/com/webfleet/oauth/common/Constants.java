package com.webfleet.oauth.common;

public class Constants {
    public static final String RANDOM_KEY_SESSION_ATTRIBUTE = "random";
    public static final String OAUTH_ACCESS_TOKEN_SESSION_ATTRIBUTE = "token";
    public static final String HAS_REFRESH_TOKEN = "hasRefreshToken";
    public static final String REDIRECT_URI_URL = "/uaa/oauth/authorize?scope={scopes}&redirect_uri={redirecturi}&client_id={clientid}&response_type=code&state={random}";
    public static final String OAUTH_REVOKE_URL = "/uaa/oauth/revoke";
    public static final String OAUTH_TOKEN_URL = "/uaa/oauth/token";
    public static final String ME_URL = "/uaa/api/me";
}
