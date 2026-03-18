package com.billpayment.payment_service.service;

import com.billpayment.payment_service.entity.PaymentEntity;
import com.billpayment.payment_service.model.PaymentResult;
import com.billpayment.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResult processPayment(String fileId, String vendorName, String amount) {
        log.info("Processing payment for fileId: {}, vendor: {}, amount: {}",
                fileId, vendorName, amount);

        // Generate unique transaction ID
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Save to database
        PaymentEntity entity = PaymentEntity.builder()
                .fileId(fileId)
                .vendorName(vendorName)
                .amount(amount)
                .transactionId(transactionId)
                .status("SUCCESS")
                .processedAt(LocalDateTime.now())
                .build();

        PaymentEntity saved = paymentRepository.save(entity);
        log.info("Payment saved to database with ID: {}", saved.getId());

        return PaymentResult.builder()
                .fileId(fileId)
                .vendorName(vendorName)
                .amount(amount)
                .transactionId(transactionId)
                .status("SUCCESS")
                .message("Payment processed successfully")
                .build();
    }
}