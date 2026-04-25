package com.supervision.livraisons.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.supervision.livraisons.dto.DeliveryStatsResponse;
import com.supervision.livraisons.dto.UpdateStatusRequest;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.service.DeliveryService;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public List<Delivery> getDeliveries(Authentication authentication) {
        String userId = currentUserId(authentication);
        String role = currentRole(authentication);
        return deliveryService.getDeliveriesForUser(userId, role);
    }

    @PostMapping
    public Delivery createDelivery(@RequestBody Delivery request, Authentication authentication) {
        String role = currentRole(authentication);
        return deliveryService.createDelivery(role, request);
    }

    @PutMapping("/{id}")
    public Delivery updateDelivery(@PathVariable String id,
                                   @RequestBody Delivery request,
                                   Authentication authentication) {
        String role = currentRole(authentication);
        return deliveryService.updateDelivery(id, role, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable String id, Authentication authentication) {
        String role = currentRole(authentication);
        deliveryService.deleteDelivery(id, role);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/today")
    public List<Delivery> getToday(Authentication authentication) {
        String userId = currentUserId(authentication);
        String role = currentRole(authentication);
        return deliveryService.getTodayDeliveries(userId, role);
    }

    @GetMapping("/stats")
    public DeliveryStatsResponse getStats(Authentication authentication) {
        String userId = currentUserId(authentication);
        String role = currentRole(authentication);
        return deliveryService.getStats(userId, role);
    }

    @GetMapping("/{id}")
    public Delivery getById(@PathVariable String id, Authentication authentication) {
        String userId = currentUserId(authentication);
        String role = currentRole(authentication);
        return deliveryService.getDeliveryById(id, userId, role);
    }

    @PatchMapping("/{id}/status")
    public Delivery updateStatus(@PathVariable String id,
                                 @RequestBody UpdateStatusRequest request,
                                 Authentication authentication) {
        String userId = currentUserId(authentication);
        String role = currentRole(authentication);
        return deliveryService.updateStatus(id, userId, role, request);
    }

    private String currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return authentication.getName();
    }

    private String currentRole(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String value = authority.getAuthority();
            if (value.startsWith("ROLE_")) {
                return value.substring(5);
            }
        }
        return "";
    }
}
