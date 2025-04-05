package ee.inbank.pas.controller;

import ee.inbank.model.CustomerPurchaseRequest;
import ee.inbank.model.CustomerPurchaseResponse;
import ee.inbank.pas.service.PurchaseApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ee.inbank.pas.util.ObjectBuildersUtil.buildResponse;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/purchase")
public class PurchaseApprovalController {

    private final PurchaseApprovalService purchaseApprovalService;

    @PostMapping("/approval")
    public ResponseEntity<CustomerPurchaseResponse> purchaseApproval(@RequestBody @Valid CustomerPurchaseRequest purchaseRequest) {
        return ResponseEntity.ok(buildResponse(purchaseApprovalService.getPurchaseApprovalResult(purchaseRequest)));
    }
}
