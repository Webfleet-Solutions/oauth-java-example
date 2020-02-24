package com.webfleet.oauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webfleet.oauth.common.Constants;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.common.RandomKey;
import com.webfleet.oauth.service.TokenStoreService;
import com.webfleet.oauth.service.feign.Authserver;
import com.webfleet.oauth.service.feign.OAuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"webfleet.clientid=dummy", "webfleet.clientsecret=dummy"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class CallbackControllerTest {

    public static final String URL_TEMPLATE = KnownUrls.CALLBACK;
    public static final String VIEW_NAME = KnownUrls.View.CALLBACK.viewName();
    public static final String EXPECTED_LOGIN_URL = "http://localhost/login";

    @MockBean
    TokenStoreService tokenStoreService;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    Authserver authserver;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        when(authserver.token(any()))
                .thenReturn(new OAuthToken());
    }

    @Test
    public void shouldRedirectToLoginForm() throws Exception {
        mockMvc.perform(get(URL_TEMPLATE))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(EXPECTED_LOGIN_URL));
    }

    @Test
    @WithMockUser("admin")
    public void shouldRenderView() throws Exception {

        final RandomKey randomKey = new RandomKey();
        mockMvc.perform(get(URL_TEMPLATE)
                .queryParam("code", "dummy")
                .queryParam("state", randomKey.getKey())
                .sessionAttr(Constants.RANDOM_KEY_SESSION_ATTRIBUTE, randomKey))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_NAME));
    }
}