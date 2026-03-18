package com.billpayment.verification_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResult {
    private String fileId;
    private String vendorName;
    private String vendorEmail;
    private String amount;
    private String dueDate;
    private boolean approved;
    private String reason;
}