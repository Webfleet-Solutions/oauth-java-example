package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.Constants;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.common.RandomKey;
import com.webfleet.oauth.service.TokenStoreService;
import com.webfleet.oauth.service.feign.Authserver;
import com.webfleet.oauth.service.feign.OAuthToken;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"webfleet.clientid=dummy", "webfleet.clientsecret=dummy"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class RefreshTokenControllerTest {

    public static final String URL_TEMPLATE = KnownUrls.REFRESH;
    public static final String VIEW_NAME = KnownUrls.View.REFRESH.viewName();
    public static final String EXPECTED_LOGIN_URL = "http://localhost/login";

    @MockBean
    TokenStoreService tokenStoreService;
    @MockBean
    Authserver authserver;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRedirectToLoginForm() throws Exception {
        mockMvc.perform(get(URL_TEMPLATE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(EXPECTED_LOGIN_URL));
    }

    @Test
    @WithMockUser("admin")
    public void shouldRedirectToConsume() throws Exception {
        final RandomKey randomKey = new RandomKey();
        when(tokenStoreService.getRefreshToken(anyString()))
                .thenReturn("dummy");
        when(authserver.token(anyMap()))
                .thenReturn(new OAuthToken());
        mockMvc.perform(get(URL_TEMPLATE)
                .sessionAttr(Constants.RANDOM_KEY_SESSION_ATTRIBUTE, randomKey))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(KnownUrls.CONSUME));
    }

    @Test
    @WithMockUser("admin")
    public void shouldRedirectToLinkAccount() throws Exception {
        final RandomKey randomKey = new RandomKey();
        when(tokenStoreService.getRefreshToken(anyString()))
                .thenReturn(null);
        mockMvc.perform(get(URL_TEMPLATE)
                .sessionAttr(Constants.RANDOM_KEY_SESSION_ATTRIBUTE, randomKey))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(KnownUrls.SERVICE));
    }

    @Test
    @WithMockUser("admin")
    public void shouldRenderView() throws Exception {
        final RandomKey randomKey = new RandomKey();
        when(tokenStoreService.getRefreshToken(anyString()))
                .thenReturn("dummy");
        final FeignException.BadRequest mock = mock(FeignException.BadRequest.class);
        when(mock.status()).thenReturn(400);
        when(mock.getMessage()).thenReturn("Bad request");
        doThrow(mock)
                .when(authserver)
                .token(any());
//        Mockito.when(authserver.token(ArgumentMatchers.any()))
//                .thenThrow(mock);
        mockMvc.perform(get(URL_TEMPLATE)
                .sessionAttr(Constants.RANDOM_KEY_SESSION_ATTRIBUTE, randomKey))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_NAME));
    }
}