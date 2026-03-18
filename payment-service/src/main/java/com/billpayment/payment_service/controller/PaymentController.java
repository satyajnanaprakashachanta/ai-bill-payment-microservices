package com.billpayment.payment_service.controller;

import com.billpayment.payment_service.model.PaymentResult;
import com.billpayment.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResult> processPayment(
            @RequestParam String fileId,
            @RequestParam String vendorName,
            @RequestParam String amount) {

        PaymentResult result = paymentService.processPayment(fileId, vendorName, amount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment Service is running");
    }
}