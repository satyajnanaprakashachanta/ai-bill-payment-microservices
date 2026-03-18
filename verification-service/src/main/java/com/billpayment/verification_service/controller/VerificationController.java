package com.billpayment.verification_service.controller;

import com.billpayment.verification_service.model.VerificationResult;
import com.billpayment.verification_service.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/check")
    public ResponseEntity<VerificationResult> verifyBill(
            @RequestParam String fileId,
            @RequestParam String vendorName,
            @RequestParam String amount,
            @RequestParam String dueDate) {

        VerificationResult result = verificationService.verify(
                fileId, vendorName, amount, dueDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Verification Service is running");
    }
}