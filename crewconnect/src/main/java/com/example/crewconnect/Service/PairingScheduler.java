package com.example.crewconnect.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PairingScheduler {
    private final PairingService pairingService;
    public PairingScheduler(PairingService pairingService) { this.pairingService = pairingService; }

    @Scheduled(fixedRate = 60_000) // every minute
    public void run() { pairingService.autoPairNextDays(7); }
}