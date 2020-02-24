package com.webfleet.oauth.service.feign;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;


/**
 * Simple POJO class with some of the OAuth fields, additional details are not mapped since we don't need them for this project.
 * Notably this also allows us to avoid some oauth2 dependencies overhead. In a real application we will probably have some framework
 * which provides accessors to all token's fields.
 */
@JsonDeserialize(using = OAuthTokenJackson2Deserializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthToken implements Serializable {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String SCOPE = "scope";

    @JsonProperty("scope")
    private Set<String> scope;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType;
    private boolean isExpired;
    private Date expiration;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("access_token")
    private String value;
    private Map<String, Object> additionalInformation;

    public OAuthToken(OAuthToken token) {
        this.scope = Collections.unmodifiableSet(token.scope);
        this.refreshToken = token.refreshToken;
        this.expiration = new Date(token.expiration.getTime());
        this.expiresIn = token.expiresIn;
        this.tokenType = token.tokenType;
        this.value = token.value;
        this.isExpired = token.isExpired;
    }

    public OAuthToken() {

    }

    public OAuthToken(final String value) {
        this.value = value;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(final Set<String> scope) {
        this.scope = scope;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(final Date expiration) {
        this.expiration = new Date(expiration.getTime());
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public void setAdditionalInformation(final Map<String, Object> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
