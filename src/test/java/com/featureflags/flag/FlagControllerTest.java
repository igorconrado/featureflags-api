package com.featureflags.flag;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FlagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String getToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test","email":"flag-test@test.com","password":"123456"}
                                """))
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void createFlag_shouldReturn201() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"key":"my-flag","name":"My Flag"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.key").value("my-flag"))
                .andExpect(jsonPath("$.name").value("My Flag"))
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    void createFlag_invalidKey_shouldReturn400() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"key":"BAD KEY!","name":"Bad"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFlag_duplicateKey_shouldReturn400() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"key":"dup-flag","name":"First"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"key":"dup-flag","name":"Second"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void enableFlag_shouldReturn200_withEnabledTrue() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"key":"enable-test","name":"Enable Test"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/flags/enable-test/enable")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void setRollout_shouldReturn200_withPercentage() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"key":"rollout-test","name":"Rollout Test"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/flags/rollout-test/rollout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"percentage":50}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rolloutPercentage").value(50));
    }

    @Test
    void deleteFlag_shouldReturn204() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"key":"delete-test","name":"Delete Test"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/flags/delete-test")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
