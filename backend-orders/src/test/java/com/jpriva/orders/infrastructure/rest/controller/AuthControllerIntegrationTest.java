package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.TestcontainersConfiguration;
import com.jpriva.orders.application.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserDto.CreateRequest userRequest = new UserDto.CreateRequest(
                "test.user@example.com",
                "P@ssw0rd123",
                "Test User",
                "1234567890",
                "123 Test Street",
                "EXTERNAL"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.email").value(userRequest.email()))
                .andExpect(jsonPath("$.fullName").value(userRequest.fullName()));
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        UserDto.CreateRequest userRequest = new UserDto.CreateRequest(
                "login.user@example.com",
                "P@ssw0rd123",
                "Login User",
                "0987654321",
                "456 Login Avenue",
                "ADMIN"
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated());

        UserDto.LoginRequest loginRequest = new UserDto.LoginRequest(
                "login.user@example.com",
                "P@ssw0rd123"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print());
    }
}
