package com.zerobin.zerobin_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntryDto {

    private Long id;
    private String name;
    private String initials;
    private int points;
    private int rank;
    private StatsDto stats;

}
