package com.metanet.metabus.mileage.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MileageDto {
    private String name;
    private Long point;
    private String saveStatus;
    private LocalDate createdDate;
}

