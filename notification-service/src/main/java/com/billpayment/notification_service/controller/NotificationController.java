package com.billpayment.notification_service.controller;

import com.billpayment.notification_service.model.NotificationResult;
import com.billpayment.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<NotificationResult> sendNotification(
            @RequestParam String fileId,
            @RequestParam String transactionId,
            @RequestParam String recipientEmail,
            @RequestParam String amount,
            @RequestParam String vendorName) {

        NotificationResult result = notificationService.sendNotification(
                fileId, transactionId, recipientEmail, amount, vendorName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running");
    }
}