package com.techchallenge.infrastructure.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@ExtendWith(SpringExtension.class)
class RedirectApiTest {

    MockMvc mockMvc;

    RedirectApi redirectApi;

    @BeforeEach
    void init(){
        redirectApi = new RedirectApi();
        mockMvc = MockMvcBuilders.standaloneSetup(redirectApi)
                .setControllerAdvice().build();
    }

    @Test
    void swagger() throws Exception {
       mockMvc.perform(get("/"))
                .andExpect(redirectedUrl("swagger-ui.html"));
    }
}