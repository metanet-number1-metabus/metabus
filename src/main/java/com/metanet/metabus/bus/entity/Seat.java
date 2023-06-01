package com.metanet.metabus.bus.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long seatNum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", foreignKey = @ForeignKey(name = "fk_seat_bus"))
    private Bus bus;

    public static Seat of(Long seatNum, Bus bus) {
        return Seat.builder()
                .seatNum(seatNum)
                .bus(bus)
                .build();
    }
}
