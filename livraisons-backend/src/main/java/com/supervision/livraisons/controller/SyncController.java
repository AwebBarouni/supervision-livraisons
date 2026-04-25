package com.supervision.livraisons.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supervision.livraisons.dto.SyncUpdateStatusRequest;
import com.supervision.livraisons.dto.StartDaySyncResponse;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.service.SyncService;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @GetMapping("/start-day")
    public StartDaySyncResponse startDay(Authentication authentication) {
        String userId = currentUserId(authentication);
        String role = currentRole(authentication);
        return syncService.getStartDaySync(userId, role);
    }

    @GetMapping("/daily/{driverId}")
    public List<Delivery> dailySync(@PathVariable String driverId,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                    @RequestParam(required = false) Double lat,
                                    @RequestParam(required = false) Double lng,
                                    Authentication authentication) {
        String userId = currentUserId(authentication);
        String role = currentRole(authentication);
        return syncService.getDailyDeliveries(driverId, date, lat, lng, userId, role);
    }

    @GetMapping("/emergency-messages")
    public List<Message> emergencyMessages(Authentication authentication) {
        String userId = currentUserId(authentication);
        return syncService.getEmergencyMessages(userId);
    }

    @PostMapping("/update-status")
    public Delivery updateStatus(@RequestBody SyncUpdateStatusRequest request, Authentication authentication) {
        String userId = currentUserId(authentication);
        String role = currentRole(authentication);
        return syncService.updateStatusFromSync(request, userId, role);
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
