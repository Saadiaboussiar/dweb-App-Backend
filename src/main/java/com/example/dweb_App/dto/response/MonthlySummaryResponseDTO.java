package com.example.dweb_App.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class MonthlySummaryResponseDTO {
    private Long id;
    private Long technicianId;
    private LocalDate monthYear;
    private Integer totalPoints;
    private BigDecimal totalBonus;
    private Integer interventionsCount;
    private LocalDateTime lastUpdated;
}
