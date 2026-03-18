package com.billpayment.notification_service.service;

import com.billpayment.notification_service.entity.NotificationEntity;
import com.billpayment.notification_service.model.NotificationResult;
import com.billpayment.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationResult sendNotification(String fileId, String transactionId,
                                               String recipientEmail, String amount,
                                               String vendorName) {
        log.info("Sending notification to: {} for transaction: {}",
                recipientEmail, transactionId);

        // Save to database
        NotificationEntity entity = NotificationEntity.builder()
                .fileId(fileId)
                .transactionId(transactionId)
                .recipientEmail(recipientEmail)
                .amount(amount)
                .vendorName(vendorName)
                .status("SENT")
                .sentAt(LocalDateTime.now())
                .build();

        NotificationEntity saved = notificationRepository.save(entity);
        log.info("Notification saved to database with ID: {}", saved.getId());

        log.info("Email sent to: {}", recipientEmail);
        log.info("Subject: Payment Confirmation - {}", transactionId);
        log.info("Body: Your payment of ${} to {} has been processed successfully.",
                amount, vendorName);

        return NotificationResult.builder()
                .fileId(fileId)
                .transactionId(transactionId)
                .recipientEmail(recipientEmail)
                .status("SENT")
                .message("Notification sent successfully to " + recipientEmail)
                .build();
    }
}