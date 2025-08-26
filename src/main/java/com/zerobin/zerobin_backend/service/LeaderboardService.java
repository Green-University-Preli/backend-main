package com.zerobin.zerobin_backend.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zerobin.zerobin_backend.dto.LeaderboardEntryDto;
import com.zerobin.zerobin_backend.dto.StatsDto;
import com.zerobin.zerobin_backend.entity.User;
import com.zerobin.zerobin_backend.entity.WasteReport;
import com.zerobin.zerobin_backend.repository.UserRepository;
import com.zerobin.zerobin_backend.repository.WasteReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final WasteReportRepository wasteReportRepository;

    public List<LeaderboardEntryDto> getLeaderboard() {
        List<User> users = userRepository.findAll();

        List<LeaderboardEntryDto> entries = users.stream().map(user -> {
            List<WasteReport> reports = wasteReportRepository.findByUser_Id(user.getId());

            int challengesCompleted = reports.size();
            int daysActive = (int) reports.stream()
                    .map(r -> r.getCreatedAt().toLocalDate())
                    .distinct()
                    .count();

            // Example conversion: 1 point = 0.1 kg (adjust as you like)
            double wasteReducedKg = user.getPoints() * 0.1;
            String wasteReduced = wasteReducedKg >= 1000
                    ? String.format("%.1f tons", wasteReducedKg / 1000.0)
                    : String.format("%.1f kg", wasteReducedKg);

            String communityImpact = user.getPoints() > 2000 ? "High"
                    : (user.getPoints() > 500 ? "Medium" : "Low");

            StatsDto stats = new StatsDto(
                    wasteReduced,
                    challengesCompleted,
                    daysActive,
                    communityImpact
            );

            return new LeaderboardEntryDto(
                    user.getId(),
                    (safe(user.getFirstName()) + " " + safe(user.getLastName())).trim(),
                    initials(user.getFirstName(), user.getLastName()),
                    user.getPoints(),
                    0, // temporary; we set rank after sort
                    stats
            );
        }).collect(Collectors.toList());

        // Sort by points desc and assign rank
        entries.sort(Comparator.comparingInt(LeaderboardEntryDto::getPoints).reversed());
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }

        return entries;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String initials(String first, String last) {
        char a = safe(first).isEmpty() ? ' ' : safe(first).charAt(0);
        char b = safe(last).isEmpty() ? ' ' : safe(last).charAt(0);
        return (String.valueOf(a) + b).trim().toUpperCase();
    }
}
