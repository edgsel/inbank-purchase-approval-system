package ee.inbank.pas.controller;

import ee.inbank.pas.PurchaseApprovalSystemApplication;
import ee.inbank.pas.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ee.inbank.pas.testcontainers.AbstractIntegrationTest;

import java.util.stream.Stream;

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

    @Test
    @DisplayName("GIVEN valid request but ineligible customer WHEN request is sent THEN returns successful operation, but purchase rejected")
    public void ineligibleCustomerRequestReturnsSuccessfulOperation() throws Exception {
        var request = readFromFileToString("/purchase-approval/purchase-approval-ineligible-customer-request.json");

        mockMvc.perform(
                post(URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.approved").value(false))
            .andExpect(jsonPath("$.amount").value(400.00));
    }

    @Test
    @DisplayName("GIVEN unknown customer WHEN request is sent THEN returns not found")
    public void unknownCustomerRequestReturnsNotFound() throws Exception {
        var request = readFromFileToString("/purchase-approval/purchase-approval-unknown-customer-request.json");

        mockMvc.perform(
                post(URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(ErrorCode.CUSTOMER_NOT_FOUND.name()))
            .andExpect(jsonPath("$.description").value("Customer with personal ID 12345 not found"));
    }

    @Test
    @DisplayName("GIVEN empty request WHEN request is sent THEN returns validation error")
    public void emptyRequestReturnsValidationError() throws Exception {

        mockMvc.perform(
                post(URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}")
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name()));
    }

    @ParameterizedTest(name = "{index} => Request: \"{0}\" should return validation error")
    @MethodSource("provideTestDataForValidationTests")
    @DisplayName("GIVEN invalid data WHEN request is sent THEN should return validation error")
    public void invalidRequestReturnsValidationError(String request, String expectedDescription) throws Exception {
        mockMvc.perform(
                post(URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name()))
            .andExpect(jsonPath("$.description").value(expectedDescription));
    }

    private static Stream<Arguments> provideTestDataForValidationTests() {
        return Stream.of(
            Arguments.of(
                readFromFileToString("/purchase-approval/purchase-approval-missing-personal-id-request.json"),
                "personalId: must not be null"
            ),
            Arguments.of(
                readFromFileToString("/purchase-approval/purchase-approval-missing-amount-request.json"),
                "amount: must not be null"
            ),
            Arguments.of(
                readFromFileToString("/purchase-approval/purchase-approval-missing-period-request.json"),
                "paymentPeriodInMonths: must not be null"
            ),
            Arguments.of(
                readFromFileToString("/purchase-approval/purchase-approval-amount-is-under-entry-threshold-request.json"),
                "amount: must be greater than or equal to 200.0"
            ),
            Arguments.of(
                readFromFileToString("/purchase-approval/purchase-approval-amount-is-over-limit-request.json"),
                "amount: must be less than or equal to 5000.0"
            ),
            Arguments.of(
                readFromFileToString("/purchase-approval/purchase-approval-period-is-under-entry-threshold-request.json"),
                "paymentPeriodInMonths: must be greater than or equal to 6"
            ),
            Arguments.of(
                readFromFileToString("/purchase-approval/purchase-approval-period-is-over-limit-request.json"),
                "paymentPeriodInMonths: must be less than or equal to 24"
            )
        );
    }
}
