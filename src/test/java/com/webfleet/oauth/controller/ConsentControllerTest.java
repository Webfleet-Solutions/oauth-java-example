package com.webfleet.oauth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {"webfleet.clientid=dummy", "webfleet.clientsecret=dummy"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ConsentControllerTest
{

    public static final String URL_TEMPLATE = "/consent";
    public static final String VIEW_NAME = "consent";
    public static final String EXPECTED_LOGIN_URL = "http://localhost/login";
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRedirectToLoginForm() throws Exception
    {
        mockMvc.perform(get(URL_TEMPLATE))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(EXPECTED_LOGIN_URL));
    }

    @Test
    @WithMockUser("admin")
    public void shouldRenderView() throws Exception
    {
        mockMvc.perform(get(URL_TEMPLATE))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_NAME));
    }
}