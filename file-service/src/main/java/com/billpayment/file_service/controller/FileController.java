package com.billpayment.file_service.controller;

import com.billpayment.file_service.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadBill(@RequestParam("file") MultipartFile file) {
        String fileId = fileService.saveBill(file);
        return ResponseEntity.ok("Bill uploaded successfully. File ID: " + fileId);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("File Service is running");
    }
}