package com.metanet.metabus.bus.entity;

import com.metanet.metabus.common.BaseEntity;
import com.metanet.metabus.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_reservation_member"))
    private Member member;

    private String departure;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private LocalDate departureDate;
    private Long payment;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", foreignKey = @ForeignKey(name = "fk_reservation_seat"))
    private Seat seatId;

    private String passengerType;
}
