package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.Constants;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.common.RandomKey;
import com.webfleet.oauth.service.TokenStoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"webfleet.clientid=dummy", "webfleet.clientsecret=dummy"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class LinkAccountControllerTest
{

    public static final String URL_TEMPLATE = KnownUrls.LINK_ACCOUNT;
    public static final String EXPECTED_LOGIN_URL = "http://localhost/login";

    @MockBean
    TokenStoreService tokenStoreService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRedirectToLoginForm() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_TEMPLATE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(EXPECTED_LOGIN_URL));
    }

    @Test
    @WithMockUser("admin")
    public void shouldRedirectToConsent() throws Exception
    {
        final RandomKey randomKey = new RandomKey();
        when(tokenStoreService.getRefreshToken(anyString()))
                .thenReturn("dummy");
        mockMvc.perform(get(URL_TEMPLATE)
                .sessionAttr(Constants.RANDOM_KEY_SESSION_ATTRIBUTE, randomKey))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(KnownUrls.CONSENT));
    }

    @Test
    @WithMockUser("admin")
    public void shouldRedirectToAuthserver() throws Exception
    {
        final RandomKey randomKey = new RandomKey();
        mockMvc.perform(get(URL_TEMPLATE)
                .sessionAttr(Constants.RANDOM_KEY_SESSION_ATTRIBUTE, randomKey))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("http*://**/uaa/oauth/**"));
    }
}