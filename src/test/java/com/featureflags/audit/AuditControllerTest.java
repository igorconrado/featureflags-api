package com.featureflags.audit;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String getToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test","email":"audit-test@test.com","password":"123456"}
                                """))
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void getAuditLogs_shouldReturn200() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/audit")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAuditLogsByFlagKey_shouldReturn200_withEntries() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"key":"audit-flag","name":"Audit Flag"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/audit/audit-flag")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].flagKey").value("audit-flag"))
                .andExpect(jsonPath("$[0].action").value("CREATED"));
    }
}
