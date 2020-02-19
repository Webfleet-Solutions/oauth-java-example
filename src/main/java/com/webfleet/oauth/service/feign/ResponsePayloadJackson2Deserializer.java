package com.webfleet.oauth.service.feign;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.webfleet.oauth.service.feign.ResponsePayload;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public final class ResponsePayloadJackson2Deserializer extends StdDeserializer<ResponsePayload>
{

    public ResponsePayloadJackson2Deserializer()
    {
        super(ResponsePayload.class);
    }

    @Override
    public ResponsePayload deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {

        String tokenValue = null;
        String tokenType = null;
        String refreshToken = null;
        Long expiresIn = null;
        Set<String> scope = null;
        Map<String, Object> additionalInformation = new LinkedHashMap<>();

        // TODO What should occur if a parameter exists twice
        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String name = jp.getCurrentName();
            jp.nextToken();
            if (ResponsePayload.ACCESS_TOKEN.equals(name))
            {
                tokenValue = jp.getText();
            } else if (ResponsePayload.TOKEN_TYPE.equals(name))
            {
                tokenType = jp.getText();
            } else if (ResponsePayload.REFRESH_TOKEN.equals(name))
            {
                refreshToken = jp.getText();
            } else if (ResponsePayload.EXPIRES_IN.equals(name))
            {
                try
                {
                    expiresIn = jp.getLongValue();
                } catch (JsonParseException e)
                {
                    expiresIn = Long.valueOf(jp.getText());
                }
            } else if (ResponsePayload.SCOPE.equals(name))
            {
                scope = parseScope(jp);
            } else
            {
                additionalInformation.put(name, jp.readValueAs(Object.class));
            }
        }

        // TODO What should occur if a required parameter (tokenValue or tokenType) is missing?
        ResponsePayload accessToken = new ResponsePayload(tokenValue);
        accessToken.setTokenType(tokenType);
        if (expiresIn != null)
        {
            accessToken.setExpiration(new Date(System.currentTimeMillis() + (expiresIn * 1000)));
        }
        if (refreshToken != null)
        {
            accessToken.setRefreshToken(refreshToken);
        }
        accessToken.setScope(scope);
        accessToken.setAdditionalInformation(additionalInformation);

        return accessToken;
    }

    private Set<String> parseScope(JsonParser jp) throws IOException
    {
        Set<String> scope;
        if (jp.getCurrentToken() == JsonToken.START_ARRAY)
        {
            scope = new TreeSet<>();
            while (jp.nextToken() != JsonToken.END_ARRAY)
            {
                scope.add(jp.getValueAsString());
            }
        } else
        {
            String text = jp.getText();
            scope = parseParameterList(text);
        }
        return scope;
    }

    private Set<String> parseParameterList(String values)
    {
        Set<String> result = new TreeSet<>();
        if (values != null && values.trim().length() > 0)
        {
            // the spec says the scope is separated by spaces
            String[] tokens = values.split("[\\s+]");
            result.addAll(Arrays.asList(tokens));
        }
        return result;
    }
}