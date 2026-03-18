package com.billpayment.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResult {
    private String fileId;
    private String transactionId;
    private String recipientEmail;
    private String status;
    private String message;
}