package com.example.crewconnect.Service;

import com.example.crewconnect.Database.Pairing;
import com.example.crewconnect.Repository.PairingRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CleanupService {

    private final PairingRepository pairingRepo;

    public CleanupService(PairingRepository pairingRepo) {
        this.pairingRepo = pairingRepo;
    }

    // runs every 5 minutes
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void updateExpiredPairings() {
        LocalDateTime now = LocalDateTime.now();

        List<Pairing> expired =
                pairingRepo.findByStatusAndEndBefore("SCHEDULED", now);

        for (Pairing p : expired) {
            p.setStatus("COMPLETED");
        }

        pairingRepo.saveAll(expired);
    }
}