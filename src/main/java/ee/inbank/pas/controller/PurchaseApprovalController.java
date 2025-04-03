package ee.inbank.pas.controller;

import ee.inbank.model.CustomerPurchaseRequest;
import ee.inbank.model.CustomerPurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/purchase")
@RequiredArgsConstructor
public class PurchaseApprovalController {

    @PostMapping("/approval")
    public ResponseEntity<CustomerPurchaseResponse> purchaseApproval(@RequestBody CustomerPurchaseRequest request) {
        return ResponseEntity.ok(CustomerPurchaseResponse.builder().build());
    }
}
