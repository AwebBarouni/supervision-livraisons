package com.supervision.livraisons.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supervision.livraisons.dto.SeedResponse;
import com.supervision.livraisons.seeder.DataSeeder;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    private final DataSeeder dataSeeder;

    public SetupController(DataSeeder dataSeeder) {
        this.dataSeeder = dataSeeder;
    }

    @PostMapping("/seed")
    public SeedResponse seed(@RequestParam(name = "force", defaultValue = "false") boolean force) {
        if (force) {
            dataSeeder.resetAndSeed();
            return new SeedResponse(true, "Demo database reset and seeded.");
        }

        boolean seeded = dataSeeder.seedIfEmpty();
        if (seeded) {
            return new SeedResponse(true, "Demo database seeded.");
        }

        return new SeedResponse(false, "Database already initialized.");
    }
}
