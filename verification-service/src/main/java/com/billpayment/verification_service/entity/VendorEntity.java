package com.billpayment.verification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vendors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String vendorName;
    private String vendorEmail;
    private String bankAccount;
    private String status;
}