package com.featureflags.evaluation;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String getToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test","email":"eval-test@test.com","password":"123456"}
                                """))
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    private void createFlag(String token, String key, String name) throws Exception {
        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(String.format("""
                                {"key":"%s","name":"%s"}
                                """, key, name)))
                .andExpect(status().isCreated());
    }

    private void enableFlag(String token, String key) throws Exception {
        mockMvc.perform(patch("/api/flags/" + key + "/enable")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void evaluate_disabledFlag_shouldReturnFalse() throws Exception {
        String token = getToken();
        createFlag(token, "disabled-flag", "Disabled");

        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flagKey":"disabled-flag","userId":"user-1","environment":"production"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.reason").value("Flag is disabled"));
    }

    @Test
    void evaluate_allowedUser_shouldReturnTrue() throws Exception {
        String token = getToken();
        createFlag(token, "allowed-test", "Allowed Test");
        enableFlag(token, "allowed-test");

        mockMvc.perform(put("/api/flags/allowed-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"name":"Allowed Test","allowedUsers":["user-123"]}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flagKey":"allowed-test","userId":"user-123","environment":"production"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.reason").value("User in allowed list"));
    }

    @Test
    void evaluate_wrongEnvironment_shouldReturnFalse() throws Exception {
        String token = getToken();
        createFlag(token, "env-test", "Env Test");
        enableFlag(token, "env-test");

        mockMvc.perform(put("/api/flags/env-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"name":"Env Test","environments":["production"]}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flagKey":"env-test","userId":"user-1","environment":"development"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.reason").value("Environment not allowed"));
    }

    @Test
    void evaluate_fullRollout_shouldReturnTrue() throws Exception {
        String token = getToken();
        createFlag(token, "full-rollout", "Full Rollout");
        enableFlag(token, "full-rollout");

        mockMvc.perform(patch("/api/flags/full-rollout/rollout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"percentage":100}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flagKey":"full-rollout","userId":"any-user","environment":"production"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.reason").value("Full rollout"));
    }

    @Test
    void evaluateBulk_shouldReturnMapWithResults() throws Exception {
        String token = getToken();
        createFlag(token, "bulk-one", "Bulk One");
        enableFlag(token, "bulk-one");

        mockMvc.perform(patch("/api/flags/bulk-one/rollout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"percentage":100}
                                """))
                .andExpect(status().isOk());

        createFlag(token, "bulk-two", "Bulk Two");

        mockMvc.perform(post("/api/evaluate/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flagKeys":["bulk-one","bulk-two","non-existent"],"userId":"user-1","environment":"production"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.bulk-one.enabled").value(true))
                .andExpect(jsonPath("$.results.bulk-two.enabled").value(false))
                .andExpect(jsonPath("$.results.non-existent.enabled").value(false))
                .andExpect(jsonPath("$.results.non-existent.reason").value("Flag not found"));
    }

    @Test
    void evaluate_nonExistentFlag_shouldReturnFalse() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flagKey":"does-not-exist","userId":"user-1","environment":"production"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.reason").value("Flag not found"));
    }
}
