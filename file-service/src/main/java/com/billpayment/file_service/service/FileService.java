package com.billpayment.file_service.service;

import com.billpayment.file_service.entity.BillEntity;
import com.billpayment.file_service.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final BillRepository billRepository;

    public String saveBill(MultipartFile file) {
        log.info("Saving bill: {} | Size: {} bytes",
                file.getOriginalFilename(),
                file.getSize());

        BillEntity bill = BillEntity.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .status("UPLOADED")
                .uploadedAt(LocalDateTime.now())
                .build();

        BillEntity saved = billRepository.save(bill);

        log.info("Bill saved to database with ID: {}", saved.getFileId());
        return saved.getFileId();
    }
}