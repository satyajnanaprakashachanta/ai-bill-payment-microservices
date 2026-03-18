package com.billpayment.ai_service.service;
import org.apache.pdfbox.Loader;
import com.billpayment.ai_service.entity.BillDetailsEntity;
import com.billpayment.ai_service.model.BillDetails;
import com.billpayment.ai_service.repository.BillDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private final BillDetailsRepository billDetailsRepository;
    private final RestTemplate restTemplate;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.api.model}")
    private String groqModel;

    public BillDetails extractBillDetails(String fileId, MultipartFile file) {
        log.info("Starting bill processing for fileId: {}", fileId);

        try {
            String fileContent;
            String fileName = file.getOriginalFilename() != null ?
                    file.getOriginalFilename().toLowerCase() : "";

            if (fileName.endsWith(".pdf")) {
                log.info("Extracting text from PDF...");
                try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    fileContent = stripper.getText(document);
                }
            } else {
                fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            }

            log.info("Extracted text length: {} characters", fileContent.length());
            log.info("First 500 chars: {}",
                    fileContent.substring(0, Math.min(500, fileContent.length())));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            String prompt = """
                    You are a bill processing assistant. Analyze the following document content and determine if it is a bill or invoice.
                    
                    Document content:
                    """ + fileContent.substring(0, Math.min(fileContent.length(), 2000)) + """
                    
                    If this is a bill or invoice, extract these details and respond in this EXACT JSON format:
                    {
                        "isBill": true,
                        "vendorName": "exact vendor/company name",
                        "amount": "total amount as number only",
                        "dueDate": "due date in YYYY-MM-DD format"
                    }
                    
                    If this is NOT a bill or invoice, respond with:
                    {
                        "isBill": false,
                        "vendorName": null,
                        "amount": null,
                        "dueDate": null
                    }
                    
                    Respond with ONLY the JSON, no extra text.
                    """;

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", groqModel);
            requestBody.put("messages", List.of(message));
            requestBody.put("temperature", 0.1);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    groqApiUrl, request, Map.class);

            Map responseBody = response.getBody();
            List choices = (List) responseBody.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map messageResponse = (Map) firstChoice.get("message");
            String content = (String) messageResponse.get("content");
            log.info("Groq response: {}", content);

            boolean isBill = content.contains("\"isBill\": true") ||
                    content.contains("\"isBill\":true");

            if (!isBill) {
                log.warn("File {} is not a bill", fileId);
                return BillDetails.builder()
                        .fileId(fileId)
                        .vendorName(null)
                        .amount(null)
                        .dueDate(null)
                        .status("REJECTED")
                        .build();
            }

            String vendorName = extractField(content, "vendorName");
            String amount = extractField(content, "amount");
            String dueDate = extractField(content, "dueDate");

            log.info("Extracted - vendorName: {}, amount: {}, dueDate: {}",
                    vendorName, amount, dueDate);

            BillDetailsEntity entity = BillDetailsEntity.builder()
                    .fileId(fileId)
                    .vendorName(vendorName)
                    .amount(amount)
                    .dueDate(dueDate)
                    .status("EXTRACTED")
                    .extractedAt(LocalDateTime.now())
                    .build();

            BillDetailsEntity saved = billDetailsRepository.save(entity);
            log.info("Bill details saved with ID: {}", saved.getId());

            return BillDetails.builder()
                    .fileId(fileId)
                    .vendorName(vendorName)
                    .amount(amount)
                    .dueDate(dueDate)
                    .status("EXTRACTED")
                    .build();

        } catch (Exception e) {
            log.error("AI extraction failed: {}", e.getMessage(), e);
            return BillDetails.builder()
                    .fileId(fileId)
                    .vendorName(null)
                    .amount(null)
                    .dueDate(null)
                    .status("EXTRACTION_FAILED")
                    .build();
        }
    }

    private String extractField(String json, String field) {
        try {
            String patternStr = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
            Pattern p = Pattern.compile(patternStr);
            Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
            return "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }
}