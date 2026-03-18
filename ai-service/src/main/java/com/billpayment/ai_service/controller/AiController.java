package com.billpayment.ai_service.controller;

import com.billpayment.ai_service.model.BillDetails;
import com.billpayment.ai_service.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/extract")
    public ResponseEntity<BillDetails> extractBill(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileId") String fileId) {
        BillDetails details = aiService.extractBillDetails(fileId, file);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Service is running");
    }
}