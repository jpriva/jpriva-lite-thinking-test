package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.TestcontainersConfiguration;
import com.jpriva.orders.application.dto.*;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private UUID companyId;
    private UUID clientId;

    @BeforeEach
    void setup() throws Exception {
        UserDto.CreateRequest adminUserRequest = new UserDto.CreateRequest(
                "order.admin@example.com",
                "AdminP@ssw0rd",
                "Order Admin User",
                "1112223334",
                "Admin Address",
                "ADMIN"
        );
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID adminUserId = UUID.fromString(objectMapper.readTree(registerResult.getResponse().getContentAsString()).get("id").asString());

        UserDto.LoginRequest adminLoginRequest = new UserDto.LoginRequest(
                "order.admin@example.com",
                "AdminP@ssw0rd"
        );
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        adminToken = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("accessToken").asString();

        // 2. Create Company
        CompanyDto.CreateRequest companyRequest = new CompanyDto.CreateRequest(
                "Test Company for Order",
                "123456789-0",
                "Company Address",
                "9998887776"
        );
        MvcResult companyResult = mockMvc.perform(post("/api/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        companyId = UUID.fromString(objectMapper.readTree(companyResult.getResponse().getContentAsString()).get("id").asString());

        CategoryDto.CreateRequest categoryRequest = new CategoryDto.CreateRequest(
                companyId,
                "Test Category for Order",
                "Description for Order Category"
        );
        MvcResult categoryResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID categoryId = UUID.fromString(objectMapper.readTree(categoryResult.getResponse().getContentAsString()).get("id").asString());

        ProductDto.CreateRequest productRequest = new ProductDto.CreateRequest(
                companyId,
                categoryId,
                "Test Product for Order",
                "PROD-ORDER-001",
                "Description for Order Product"
        );
        MvcResult productResult = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID productId = UUID.fromString(objectMapper.readTree(productResult.getResponse().getContentAsString()).get("id").asString());

        ProductDto.UpdatePriceRequest updatePriceRequest = new ProductDto.UpdatePriceRequest(BigDecimal.valueOf(100.00), "USD");
        mockMvc.perform(put("/api/products/" + productId + "/price")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePriceRequest)))
                .andExpect(status().isOk());

        ClientDto.CreateRequest clientRequest = new ClientDto.CreateRequest(
                companyId,
                adminUserId,
                "Test Client for Order",
                "client.order@example.com",
                "5554443332",
                "Client Address"
        );
        MvcResult clientResult = mockMvc.perform(post("/api/clients")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        clientId = UUID.fromString(objectMapper.readTree(clientResult.getResponse().getContentAsString()).get("id").asString());
    }

    @Test
    void shouldCreateOrderByAdminSuccessfully() throws Exception {
        // Arrange
        OrderDto.CreateRequest orderRequest = new OrderDto.CreateRequest(
                companyId,
                clientId,
                "USD"
        );
        String orderJson = objectMapper.writeValueAsString(orderRequest);

        mockMvc.perform(post("/api/orders/admin")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.companyId").value(companyId.toString()))
                .andExpect(jsonPath("$.clientId").value(clientId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(0.0))
                .andExpect(jsonPath("$.items").isEmpty());
    }
}