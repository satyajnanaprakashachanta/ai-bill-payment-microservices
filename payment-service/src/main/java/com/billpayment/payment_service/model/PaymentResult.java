package com.billpayment.payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {
    private String fileId;
    private String vendorName;
    private String amount;
    private String transactionId;
    private String status;
    private String message;
}