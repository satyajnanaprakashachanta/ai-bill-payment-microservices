package com.billpayment.verification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fileId;
    private String vendorName;
    private String amount;
    private String dueDate;
    private boolean approved;
    private String reason;
    private LocalDateTime verifiedAt;
}
