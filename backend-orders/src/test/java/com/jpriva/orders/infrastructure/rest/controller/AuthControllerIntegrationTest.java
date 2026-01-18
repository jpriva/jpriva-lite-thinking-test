package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.TestcontainersConfiguration;
import com.jpriva.orders.application.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserDto.CreateRequest newUser = new UserDto.CreateRequest(
                "register.test@example.com",
                "Str0ngP@ssw0rd",
                "Register Test User",
                "1234567890",
                "123 Test Street",
                "EXTERNAL"
        );

        String userJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.email").value("register.test@example.com"))
                .andExpect(jsonPath("$.fullName").value("Register Test User"));
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        UserDto.CreateRequest newUser = new UserDto.CreateRequest(
                "login.test@example.com",
                "Str0ngP@ssw0rd",
                "Login Test User",
                "0987654321",
                "321 Test Avenue",
                "EXTERNAL"
        );
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        UserDto.LoginRequest loginRequest = new UserDto.LoginRequest(
                "login.test@example.com",
                "Str0ngP@ssw0rd"
        );
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}