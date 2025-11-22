package com.example.crewconnect.Controller;

import com.example.crewconnect.Database.TimeRange;
import com.example.crewconnect.Repository.TimeRangeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/availability")
public class AdminAvailabilityController {

    private final TimeRangeRepository timeRangeRepo;

    public AdminAvailabilityController(TimeRangeRepository timeRangeRepo) {
        this.timeRangeRepo = timeRangeRepo;
    }

    @GetMapping
    public String viewAvailabilityThisWeek(Model model) {

        // Define "this week" as Monday–Sunday around today
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        LocalDateTime from = monday.atStartOfDay();
        //start of the next day after Sunday
        LocalDateTime to = sunday.plusDays(1).atStartOfDay();

        // ✅ Fetch ALL time ranges, then filter in Java for anything that overlaps [from, to)
        List<TimeRange> windows = timeRangeRepo.findAll()
                .stream()
                .filter(tr -> tr.getStart() != null && tr.getEnd() != null)
                // overlap condition: end > from AND start < to
                .filter(tr -> tr.getEnd().isAfter(from) && tr.getStart().isBefore(to))
                .sorted(Comparator.comparing(TimeRange::getStart))
                .collect(Collectors.toList());

        model.addAttribute("windows", windows);
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        return "admin-availability";
    }
}