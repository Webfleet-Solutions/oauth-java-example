package com.webfleet.oauth.common;

public final class KnownUrls
{
    public static final String LINK_ACCOUNT = "/linkAccount";
    public static final String CALLBACK = "/callback";
    public static final String CONSUME = "/consume";
    public static final String CONSENT = "/consent";
    public static final String HOME = "/";
    public static final String REFRESH = "/refresh";
    public static final String REVOKE = "/revoke";
    public static final String ERROR = "/error";


    public enum View
    {
        CONSUME("consume", KnownUrls.CONSUME),
        CALLBACK("callback", KnownUrls.CALLBACK),
        CONSENT("consent", KnownUrls.CONSENT),
        REFRESH("refreshExpired", KnownUrls.REFRESH),
        HOME("home", KnownUrls.HOME);
        private final String view;
        private final String url;

        View(String view, String url)
        {
            this.view = view;
            this.url = url;
        }

        public String viewName()
        {
            return view;
        }
    }
}
