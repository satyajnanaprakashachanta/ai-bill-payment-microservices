package com.billpayment.verification_service.controller;

import com.billpayment.verification_service.entity.VendorEntity;
import com.billpayment.verification_service.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/verify/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorRepository vendorRepository;

    @PostMapping("/add")
    public ResponseEntity<VendorEntity> addVendor(@RequestBody VendorEntity vendor) {
        vendor.setStatus("ACTIVE");
        VendorEntity saved = vendorRepository.save(vendor);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VendorEntity>> getAllVendors() {
        return ResponseEntity.ok(vendorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorEntity> getVendor(@PathVariable String id) {
        return vendorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<VendorEntity> deactivateVendor(@PathVariable String id) {
        return vendorRepository.findById(id)
                .map(vendor -> {
                    vendor.setStatus("INACTIVE");
                    return ResponseEntity.ok(vendorRepository.save(vendor));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVendor(@PathVariable String id) {
        vendorRepository.deleteById(id);
        return ResponseEntity.ok("Vendor deleted");
    }
}