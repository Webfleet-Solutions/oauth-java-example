package com.webfleet.oauth.controller;

import com.webfleet.oauth.common.Constants;
import com.webfleet.oauth.common.KnownUrls;
import com.webfleet.oauth.common.RandomKey;
import com.webfleet.oauth.service.feign.Authserver;
import com.webfleet.oauth.service.TokenStoreService;
import feign.FeignException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"webfleet.clientid=dummy", "webfleet.clientsecret=dummy"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class RevokeControllerTest
{

    public static final String URL_TEMPLATE = KnownUrls.REVOKE;
    public static final String EXPECTED_LOGIN_URL = "http://localhost/login";

    @MockBean
    TokenStoreService tokenStoreService;
    @MockBean
    Authserver authserver;

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
    public void shouldRedirectToHome() throws Exception
    {
        final RandomKey randomKey = new RandomKey();
        mockMvc.perform(get(URL_TEMPLATE)
                .sessionAttr(Constants.RANDOM_KEY_SESSION_ATTRIBUTE, randomKey))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(KnownUrls.HOME));
    }

    @Test
    @WithMockUser("admin")
    public void shouldRedirectToError() throws Exception
    {
        final RandomKey randomKey = new RandomKey();
        doThrow(mock(FeignException.FeignClientException.class)).when(authserver).revoke(any());

        mockMvc.perform(get(URL_TEMPLATE)
                .sessionAttr(Constants.RANDOM_KEY_SESSION_ATTRIBUTE, randomKey))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(KnownUrls.ERROR));
    }
}