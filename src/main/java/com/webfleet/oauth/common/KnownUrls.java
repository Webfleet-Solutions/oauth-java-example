package com.webfleet.oauth.common;

public final class KnownUrls {
    public static final String CALLBACK = "/callback";
    public static final String CONSUME = "/consume";
    public static final String HOME = "/";
    public static final String REFRESH = "/refresh";
    public static final String REVOKE = "/revoke";
    public static final String ERROR = "/error";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String SERVICE = "/service";


    public enum View {
        CONSUME("consume", KnownUrls.CONSUME),
        CALLBACK("callback", KnownUrls.CALLBACK),
        SERVICE("service", KnownUrls.SERVICE),
        LOGIN("login", KnownUrls.LOGIN),
        HOME("home", KnownUrls.HOME),
        REFRESH("refresh", KnownUrls.REFRESH);
        private final String view;
        private final String url;

        View(String view, String url) {
            this.view = view;
            this.url = url;
        }

        public String viewName() {
            return view;
        }
    }
}
