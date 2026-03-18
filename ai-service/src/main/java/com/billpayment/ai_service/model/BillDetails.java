package com.billpayment.ai_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDetails {
    private String fileId;
    private String vendorName;
    private String amount;
    private String dueDate;
    private String status;
}
