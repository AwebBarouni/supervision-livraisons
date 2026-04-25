package com.supervision.livraisons.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.service.SyncService;

@RestController
@RequestMapping("/api/emergency")
public class EmergencyController {

    private final SyncService syncService;

    public EmergencyController(SyncService syncService) {
        this.syncService = syncService;
    }

    @GetMapping("/client-search")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public List<Delivery> clientSearch(@RequestParam String query) {
        return syncService.emergencyClientSearch(query);
    }
}
