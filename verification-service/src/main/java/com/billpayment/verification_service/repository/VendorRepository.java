package com.billpayment.verification_service.repository;

import com.billpayment.verification_service.entity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, String> {
    Optional<VendorEntity> findByVendorNameIgnoreCase(String vendorName);
}