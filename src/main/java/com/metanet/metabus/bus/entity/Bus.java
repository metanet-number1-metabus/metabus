package com.metanet.metabus.bus.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long busNum;
    private LocalDate departureDate;

    public static Bus of(Long busNum, LocalDate departureDate) {
        return Bus.builder()
                .busNum(busNum)
                .departureDate(departureDate)
                .build();
    }
}
