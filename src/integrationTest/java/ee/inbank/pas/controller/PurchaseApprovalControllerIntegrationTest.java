package ee.inbank.pas.controller;

import ee.inbank.pas.PurchaseApprovalSystemApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ee.inbank.pas.testcontainers.AbstractIntegrationTest;

import static ee.inbank.pas.util.FileUtil.readFromFileToString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = PurchaseApprovalSystemApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PurchaseApprovalControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String URI_PATH = "/api/v1/purchase/approval";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GIVEN valid request WHEN request is sent THEN returns successful operation")
    public void validRequestReturnsASuccessfulOperation() throws Exception {
        var request = readFromFileToString("/purchase-approval/purchase-approval-valid-request.json");

        mockMvc.perform(
                post(URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.approved").value(true))
            .andExpect(jsonPath("$.amount").value(606.06));
    }
}
