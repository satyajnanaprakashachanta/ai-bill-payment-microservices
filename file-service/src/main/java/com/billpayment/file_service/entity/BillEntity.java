package com.billpayment.file_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String fileId;

    private String fileName;
    private String fileType;
    private Long fileSize;
    private String status;
    private LocalDateTime uploadedAt;
}