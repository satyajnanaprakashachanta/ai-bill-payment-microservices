package com.billpayment.api_gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orchestrate")
@Slf4j
public class BillOrchestrationController {

    private final RestTemplate restTemplate;

    public BillOrchestrationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/process-bill")
    public ResponseEntity<Map<String, Object>> processBill(
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> result = new HashMap<>();

        try {
            byte[] fileBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();

            // Step 1: Upload file
            log.info("Step 1: Uploading file...");
            MultiValueMap<String, Object> fileBody = new LinkedMultiValueMap<>();
            fileBody.add("file", new MultipartInputStreamFileResource(
                    new ByteArrayInputStream(fileBytes), originalFilename));
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> fileRequest =
                    new HttpEntity<>(fileBody, fileHeaders);
            ResponseEntity<String> fileResponse = restTemplate.postForEntity(
                    "http://file-service:8081/api/file/upload", fileRequest, String.class);
            String fileResponseBody = fileResponse.getBody();
            String fileId = fileResponseBody.replaceAll(
                    ".*File ID: ([a-f0-9\\-]+).*", "$1");
            result.put("fileId", fileId);
            result.put("uploadStatus", "SUCCESS");
            log.info("Step 1 Done. File ID: {}", fileId);

            // Step 2: AI Extraction
            log.info("Step 2: AI Extraction...");
            MultiValueMap<String, Object> aiBody = new LinkedMultiValueMap<>();
            aiBody.add("file", new MultipartInputStreamFileResource(
                    new ByteArrayInputStream(fileBytes), originalFilename));
            aiBody.add("fileId", fileId);
            HttpHeaders aiHeaders = new HttpHeaders();
            aiHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> aiRequest =
                    new HttpEntity<>(aiBody, aiHeaders);
            ResponseEntity<Map> aiResponse = restTemplate.postForEntity(
                    "http://ai-service:8082/api/ai/extract", aiRequest, Map.class);
            Map aiData = aiResponse.getBody();
            result.put("aiExtraction", aiData);
            result.put("aiStatus", "SUCCESS");
            log.info("Step 2 Done. AI Data: {}", aiData);

            // Check if AI extraction failed
            if ("REJECTED".equals(aiData.get("status")) ||
                    "EXTRACTION_FAILED".equals(aiData.get("status"))) {
                result.put("finalStatus", "REJECTED");
                result.put("reason", "File is not a valid bill");
                return ResponseEntity.ok(result);
            }

            // Step 3: Verification
            log.info("Step 3: Verifying bill...");
            String verifyUrl = "http://verification-service:8083/api/verify/check" +
                    "?fileId=" + fileId +
                    "&vendorName=" + aiData.get("vendorName") +
                    "&amount=" + aiData.get("amount") +
                    "&dueDate=" + aiData.get("dueDate");
            ResponseEntity<Map> verifyResponse = restTemplate.postForEntity(
                    verifyUrl, null, Map.class);
            Map verifyData = verifyResponse.getBody();
            result.put("verification", verifyData);
            log.info("Step 3 Done. Verified: {}", verifyData);

            boolean approved = (Boolean) verifyData.get("approved");
            if (!approved) {
                result.put("finalStatus", "REJECTED");
                result.put("reason", verifyData.get("reason"));
                return ResponseEntity.ok(result);
            }

            // Step 4: Payment
            log.info("Step 4: Processing payment...");
            String paymentUrl = "http://payment-service:8084/api/payment/process" +
                    "?fileId=" + fileId +
                    "&vendorName=" + aiData.get("vendorName") +
                    "&amount=" + aiData.get("amount");
            ResponseEntity<Map> paymentResponse = restTemplate.postForEntity(
                    paymentUrl, null, Map.class);
            Map paymentData = paymentResponse.getBody();
            result.put("payment", paymentData);
            log.info("Step 4 Done. Payment: {}", paymentData);

            // Step 5: Notification
            log.info("Step 5: Sending notification...");
            String vendorEmail = (String) verifyData.getOrDefault(
                    "vendorEmail", "billing@vendor.com");
            String notifyUrl = "http://notification-service:8085/api/notification/send" +
                    "?fileId=" + fileId +
                    "&transactionId=" + paymentData.get("transactionId") +
                    "&recipientEmail=" + vendorEmail +
                    "&amount=" + aiData.get("amount") +
                    "&vendorName=" + aiData.get("vendorName");
            ResponseEntity<Map> notifyResponse = restTemplate.postForEntity(
                    notifyUrl, null, Map.class);
            result.put("notification", notifyResponse.getBody());
            log.info("Step 5 Done.");

            result.put("finalStatus", "SUCCESS");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Orchestration failed: {}", e.getMessage(), e);
            result.put("finalStatus", "FAILED");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}