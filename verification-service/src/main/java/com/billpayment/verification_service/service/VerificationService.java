package com.billpayment.verification_service.service;

import com.billpayment.verification_service.entity.VendorEntity;
import com.billpayment.verification_service.entity.VerificationEntity;
import com.billpayment.verification_service.model.VerificationResult;
import com.billpayment.verification_service.repository.VendorRepository;
import com.billpayment.verification_service.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository verificationRepository;
    private final VendorRepository vendorRepository;

    public VerificationResult verify(String fileId, String vendorName,
                                     String amount, String dueDate) {
        log.info("Verifying bill for fileId: {}", fileId);

        boolean isApproved = true;
        String reason = "Bill approved successfully";
        String vendorEmail = "billing@vendor.com";
        double billAmount = 0;

        // Check if amount is valid
        try {
            billAmount = Double.parseDouble(amount);
            if (billAmount <= 0) {
                isApproved = false;
                reason = "Invalid amount";
            }
        } catch (NumberFormatException e) {
            isApproved = false;
            reason = "Invalid amount format: " + amount;
            log.warn("Invalid amount format: {}", amount);
        }

        // Check if amount exceeds limit
        if (isApproved && billAmount > 10000) {
            isApproved = false;
            reason = "Amount exceeds maximum limit of $10,000";
        }

        // Check if due date is empty
        if (isApproved && (dueDate == null || dueDate.isEmpty())) {
            isApproved = false;
            reason = "Due date is missing";
        }

        // Check if due date is in the past
        if (isApproved) {
            try {
                LocalDate billDueDate = LocalDate.parse(dueDate);
                if (billDueDate.isBefore(LocalDate.now())) {
                    isApproved = false;
                    reason = "Bill due date " + dueDate + " has already passed";
                    log.warn("Bill rejected - due date {} is in the past", dueDate);
                }
            } catch (Exception e) {
                isApproved = false;
                reason = "Invalid due date format: " + dueDate;
                log.warn("Invalid due date format: {}", dueDate);
            }
        }

        // Check if vendor exists in database
        if (isApproved) {
            try {
                Optional<VendorEntity> vendor = vendorRepository
                        .findByVendorNameIgnoreCase(vendorName);
                if (vendor.isEmpty()) {
                    isApproved = false;
                    reason = "Vendor '" + vendorName + "' is not registered in the system";
                } else if (vendor.get().getStatus().equals("INACTIVE")) {
                    isApproved = false;
                    reason = "Vendor '" + vendorName + "' is inactive";
                } else {
                    vendorEmail = vendor.get().getVendorEmail();
                    log.info("Vendor found: {} with email: {}",
                            vendor.get().getVendorName(), vendorEmail);
                }
            } catch (Exception e) {
                isApproved = false;
                reason = "Error looking up vendor: " + e.getMessage();
                log.error("Vendor lookup error: {}", e.getMessage());
            }
        }

        // Save to database
        VerificationEntity entity = VerificationEntity.builder()
                .fileId(fileId)
                .vendorName(vendorName)
                .amount(amount)
                .dueDate(dueDate)
                .approved(isApproved)
                .reason(reason)
                .verifiedAt(LocalDateTime.now())
                .build();

        VerificationEntity saved = verificationRepository.save(entity);
        log.info("Verification result saved with ID: {}", saved.getId());

        return VerificationResult.builder()
                .fileId(fileId)
                .vendorName(vendorName)
                .vendorEmail(vendorEmail)
                .amount(amount)
                .dueDate(dueDate)
                .approved(isApproved)
                .reason(reason)
                .build();
    }
}