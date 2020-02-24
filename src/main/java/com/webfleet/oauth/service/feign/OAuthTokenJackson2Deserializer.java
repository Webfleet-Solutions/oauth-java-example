package com.webfleet.oauth.service.feign;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.*;


public final class OAuthTokenJackson2Deserializer extends StdDeserializer<OAuthToken> {

    public OAuthTokenJackson2Deserializer() {
        super(OAuthToken.class);
    }

    @Override
    public OAuthToken deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        String tokenValue = null;
        String tokenType = null;
        String refreshToken = null;
        Long expiresIn = null;
        Set<String> scope = null;
        Map<String, Object> additionalInformation = new LinkedHashMap<>();

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String name = jp.getCurrentName();
            jp.nextToken();
            if (OAuthToken.ACCESS_TOKEN.equals(name)) {
                tokenValue = jp.getText();
            } else if (OAuthToken.TOKEN_TYPE.equals(name)) {
                tokenType = jp.getText();
            } else if (OAuthToken.REFRESH_TOKEN.equals(name)) {
                refreshToken = jp.getText();
            } else if (OAuthToken.EXPIRES_IN.equals(name)) {
                try {
                    expiresIn = jp.getLongValue();
                } catch (JsonParseException e) {
                    expiresIn = Long.valueOf(jp.getText());
                }
            } else if (OAuthToken.SCOPE.equals(name)) {
                scope = parseScope(jp);
            } else {
                additionalInformation.put(name, jp.readValueAs(Object.class));
            }
        }

        final OAuthToken accessToken;
        if (tokenValue == null) {
            ctxt.reportPropertyInputMismatch(OAuthToken.class, OAuthToken.ACCESS_TOKEN, "Missing access_token property");
            accessToken = null;
        } else {
            accessToken = new OAuthToken(tokenValue);
        }
        if (tokenType == null) {
            ctxt.reportPropertyInputMismatch(OAuthToken.class, OAuthToken.TOKEN_TYPE, "Missing token_type property");
        } else {
            accessToken.setTokenType(tokenType);
        }

        if (expiresIn != null) {
            accessToken.setExpiration(new Date(System.currentTimeMillis() + (expiresIn * 1000)));
        }
        if (refreshToken != null) {
            accessToken.setRefreshToken(refreshToken);
        }
        accessToken.setScope(scope);
        accessToken.setAdditionalInformation(additionalInformation);
        return accessToken;
    }

    private Set<String> parseScope(JsonParser jp) throws IOException {
        Set<String> scope;
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            scope = new TreeSet<>();
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                scope.add(jp.getValueAsString());
            }
        } else {
            String text = jp.getText();
            scope = parseParameterList(text);
        }
        return scope;
    }

    private Set<String> parseParameterList(String values) {
        Set<String> result = new TreeSet<>();
        if (values != null && values.trim().length() > 0) {
            // the spec says the scope is separated by spaces
            String[] tokens = values.split("[\\s+]");
            result.addAll(Arrays.asList(tokens));
        }
        return result;
    }
}