package com.billpayment.ai_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bill_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fileId;
    private String vendorName;
    private String amount;
    private String dueDate;
    private String status;
    private LocalDateTime extractedAt;
}