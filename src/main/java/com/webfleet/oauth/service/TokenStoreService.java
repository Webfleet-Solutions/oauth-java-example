package com.webfleet.oauth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;


@Service
public class TokenStoreService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenStoreService.class);

    private final JdbcTemplate jdbcTemplate;
    private final String password;

    public TokenStoreService(final JdbcTemplate jdbcTemplate, @Value("${token.password}") String password) {
        this.jdbcTemplate = jdbcTemplate;
        this.password = password;

    }

    private String encrypt(String payload, String salt) {
        TextEncryptor textEncryptor = Encryptors.delux(password, CharBuffer.wrap(Hex.encode(salt.getBytes())));
        return textEncryptor.encrypt(payload);
    }

    private String decrypt(String payload, String salt) {
        TextEncryptor textEncryptor = Encryptors.delux(password, CharBuffer.wrap(Hex.encode(salt.getBytes())));
        return textEncryptor.decrypt(payload);
    }

    public void saveRefreshToken(String refreshToken, String username) {
        jdbcTemplate.update("INSERT INTO USERTOKENS (USERID, REFRESH_TOKEN) VALUES(?,?)",
                username,
                encrypt(refreshToken, username));
    }

    public void updateRefreshToken(String refreshToken, String username) {
        jdbcTemplate.update("UPDATE USERTOKENS SET REFRESH_TOKEN=? WHERE USERID=?",
                encrypt(refreshToken, username),
                username);
    }

    public void deleteRefreshToken(String username) {
        jdbcTemplate.update("DELETE FROM USERTOKENS WHERE USERID=?", username);
    }


    public boolean hasRefreshToken(String username) {
        return getRefreshToken(username) != null;
    }

    public String getRefreshToken(String username) {
        try {
            final String encRefreshToken = jdbcTemplate.queryForObject(
                    "SELECT REFRESH_TOKEN FROM USERTOKENS WHERE USERID=?",
                    new Object[]{username},
                    String.class);
            return decrypt(encRefreshToken, username);
        } catch (DataAccessException e) {
            LOG.info("Couldn't retrieve the refresh token, either does not exist anymore or never existed");
            return null;
        }
    }

}
