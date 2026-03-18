package com.billpayment.ai_service.repository;

import com.billpayment.ai_service.entity.BillDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillDetailsRepository extends JpaRepository<BillDetailsEntity, String> {
}