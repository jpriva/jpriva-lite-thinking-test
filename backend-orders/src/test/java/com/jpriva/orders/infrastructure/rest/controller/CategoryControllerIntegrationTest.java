package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.TestcontainersConfiguration;
import com.jpriva.orders.application.dto.CategoryDto;
import com.jpriva.orders.application.dto.CompanyDto;
import com.jpriva.orders.application.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private UUID companyId;

    @BeforeEach
    void setup() throws Exception {
        UserDto.CreateRequest adminUserRequest = new UserDto.CreateRequest(
                "category.admin@example.com",
                "AdminP@ssw0rd",
                "Category Admin User",
                "1112223335",
                "Category Admin Address",
                "ADMIN"
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUserRequest)))
                .andExpect(status().isCreated());

        UserDto.LoginRequest adminLoginRequest = new UserDto.LoginRequest(
                "category.admin@example.com",
                "AdminP@ssw0rd"
        );
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        adminToken = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("accessToken").asString();

        CompanyDto.CreateRequest companyRequest = new CompanyDto.CreateRequest(
                "Test Company for Category",
                "CAT-123456789-0",
                "Category Company Address",
                "9998887775"
        );
        MvcResult companyResult = mockMvc.perform(post("/api/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        companyId = UUID.fromString(objectMapper.readTree(companyResult.getResponse().getContentAsString()).get("id").asString());
    }

    @Test
    void shouldCreateCategorySuccessfully() throws Exception {
        CategoryDto.CreateRequest categoryRequest = new CategoryDto.CreateRequest(
                companyId,
                "Test Category Name",
                "Test Category Description"
        );

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.companyId").value(companyId.toString()))
                .andExpect(jsonPath("$.name").value("Test Category Name"));
    }
}
