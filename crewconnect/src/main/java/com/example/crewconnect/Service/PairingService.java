package com.example.crewconnect.Service;

import com.example.crewconnect.Database.Employee;
import com.example.crewconnect.Database.Notification;
import com.example.crewconnect.Database.Pairing;
import com.example.crewconnect.Database.TimeRange;
import com.example.crewconnect.Repository.EmployeeRepository;
import com.example.crewconnect.Repository.NotificationRepository;
import com.example.crewconnect.Repository.PairingRepository;
import com.example.crewconnect.Repository.TimeRangeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PairingService {

    private final EmployeeRepository employeeRepo;
    private final TimeRangeRepository timeRangeRepo;
    private final PairingRepository pairingRepo;
    private final NotificationRepository notificationRepo;

    public PairingService(EmployeeRepository employeeRepo,
                          TimeRangeRepository timeRangeRepo,
                          PairingRepository pairingRepo,
                          NotificationRepository notificationRepo) {
        this.employeeRepo = employeeRepo;
        this.timeRangeRepo = timeRangeRepo;
        this.pairingRepo = pairingRepo;
        this.notificationRepo = notificationRepo;
    }

    /**
     * Auto-pair employees for the next N days.
     *
     * Strategy (simple & explicit):
     *  1) Load all TimeRanges whose window overlaps [todayStart, todayStart + days).
     *  2) Keep only ranges that belong to EMPLOYEES (ignore manager ranges).
     *  3) For each pair of employees' ranges, if they overlap by at least some minutes,
     *     create one pairing in the overlap.
     */
    public int autoPairNextDays(int days) {
        // Start at today's midnight so we don't miss future slots today
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(days);

        // 1) Get all ranges that overlap [from, to)
        List<TimeRange> allRanges =
                timeRangeRepo.findAllByEndAfterAndStartBefore(from, to);

        // 2) Filter to only those that belong to employees (not managers)
        List<TimeRange> employeeRanges = new ArrayList<>();
        for (TimeRange tr : allRanges) {
            if (tr.getAvailability() != null &&
                    tr.getAvailability().getEmployee() != null) {
                employeeRanges.add(tr);
            }
        }

        int created = 0;

        // 3) Compare every pair of ranges for overlapping employees
        for (int i = 0; i < employeeRanges.size(); i++) {
            TimeRange r1 = employeeRanges.get(i);
            Employee e1 = r1.getAvailability().getEmployee();
            if (e1 == null) continue;

            for (int j = i + 1; j < employeeRanges.size(); j++) {
                TimeRange r2 = employeeRanges.get(j);
                Employee e2 = r2.getAvailability().getEmployee();
                if (e2 == null) continue;

                // Skip same person
                if (e1.getEmployeeID().equals(e2.getEmployeeID())) continue;

                // Compute overlap between the two windows
                LocalDateTime overlapStart = max(r1.getStart(), r2.getStart());
                LocalDateTime overlapEnd   = min(r1.getEnd(),   r2.getEnd());

                // Require at least 15 minutes of overlap
                if (!overlapEnd.isAfter(overlapStart.plusMinutes(15))) {
                    continue;
                }

                // Choose a 30-minute slot inside the overlap (or shorter if needed)
                LocalDateTime slotStart = overlapStart;
                LocalDateTime slotEnd   = slotStart.plusMinutes(30);
                if (slotEnd.isAfter(overlapEnd)) {
                    slotEnd = overlapEnd;
                }

                // Order employees by ID so uniqueness check is stable
                Employee a = e1.getEmployeeID() < e2.getEmployeeID() ? e1 : e2;
                Employee b = (a == e1) ? e2 : e1;

                // Avoid duplicates (your unique constraint also protects this)
                boolean exists = pairingRepo
                        .existsByEmployeeAAndEmployeeBAndStartAndEnd(a, b, slotStart, slotEnd);
                if (exists) continue;

                // Create pairing
                Pairing p = new Pairing();
                p.setEmployeeA(a);
                p.setEmployeeB(b);
                p.setStart(slotStart);
                p.setEnd(slotEnd);
                p.setPlatform("Teams");
                p.setMeetingLink("https://teams.microsoft.com/");   // <--
                pairingRepo.save(p);

                // Notify both parties
                notifyPair(a, b, p);
                notifyPair(b, a, p);

                created++;
            }
        }

        return created;
    }

    private LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        return (a.isAfter(b)) ? a : b;
    }

    private LocalDateTime min(LocalDateTime a, LocalDateTime b) {
        return (a.isBefore(b)) ? a : b;
    }

    private void notifyPair(Employee receiver, Employee partner, Pairing p) {
        Notification n = new Notification();
        n.setEmployee(receiver);
        n.setType("PAIRING");
        n.setMessage(
                "You have been paired with " +
                        partner.getFirstname() + " " + partner.getLastname() +
                        " from " + p.getStart() + " to " + p.getEnd() + "."
        );
        notificationRepo.save(n);
    }

    /**
     * List of pairs for the logged-in user (by login = email or username).
     * This feeds ${pairs} in pairings.html.
     */
    @Transactional(readOnly = true)
    public List<PairingView> myPairings(String login) {
        var optEmp = employeeRepo.findByEmail(login)
                .or(() -> employeeRepo.findByUsername(login));

        // If this login is NOT an employee (e.g., a pure Manager),
        // just return an empty list instead of throwing.
        if (optEmp.isEmpty()) {
            return List.of();
        }

        Employee me = optEmp.get();

        List<Pairing> raw =
                pairingRepo.findByEmployeeAOrEmployeeBOrderByStartDesc(me, me);

        List<PairingView> result = new ArrayList<>();
        for (Pairing p : raw) {
            Employee partner = p.getEmployeeA().getEmployeeID().equals(me.getEmployeeID())
                    ? p.getEmployeeB()
                    : p.getEmployeeA();

            result.add(new PairingView(
                    partner.getFirstname() + " " + partner.getLastname(),
                    p.getStart(),
                    p.getEnd(),
                    p.getPlatform(),
                    p.getStatus(),
                    p.getMeetingLink()
            ));
        }
        return result;
    }
    /**
     * Unread notifications count for top bar.
     */
    @Transactional(readOnly = true)
    public long unreadCount(String login) {
        var optEmp = employeeRepo.findByEmail(login)
                .or(() -> employeeRepo.findByUsername(login));

        if (optEmp.isEmpty()) {
            // Manager with no Employee row → no unread employee notifications
            return 0L;
        }

        Employee me = optEmp.get();
        return notificationRepo.countByEmployeeAndReadFlagFalse(me);
    }

    // DTO used by pairings.html
    public record PairingView(
            String partnerName,
            LocalDateTime start,
            LocalDateTime end,
            String platform,
            String status,
            String meetingLink       // <-- NEW
    ) {}
}