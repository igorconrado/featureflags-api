package com.featureflags.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register_firstUser_shouldGetOwnerRole() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Igor","email":"igor@test.com","password":"123456"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Igor"))
                .andExpect(jsonPath("$.email").value("igor@test.com"));
    }

    @Test
    void register_secondUser_shouldGetMemberRole() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"First","email":"first@test.com","password":"123456"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Second","email":"second@test.com","password":"123456"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void register_duplicateEmail_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Igor","email":"dup@test.com","password":"123456"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Igor2","email":"dup@test.com","password":"123456"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_validCredentials_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Igor","email":"login@test.com","password":"123456"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"login@test.com","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("login@test.com"));
    }

    @Test
    void login_wrongPassword_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Igor","email":"wrong@test.com","password":"123456"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"wrong@test.com","password":"wrongpass"}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
