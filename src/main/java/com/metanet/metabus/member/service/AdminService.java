package com.metanet.metabus.member.service;

import com.metanet.metabus.bus.dto.BusPopularRoutesRequest;
import com.metanet.metabus.bus.dto.ReservationStatusRequest;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.ReservationStatus;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.member.dto.MemberInfoRequest;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;

    public List<ReservationStatusRequest> findAllReservation() {

        List<Reservation> reservationList = reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
        List<ReservationStatusRequest> reservationStatusRequestList = new ArrayList<>();

        for (Reservation reservation : reservationList) {
            LocalDate createdDate = reservation.getCreatedDate().toLocalDate();

            LocalDate deleteDate = null;

            if(reservation.getDeletedDate() != null){
                deleteDate = reservation.getDeletedDate().toLocalDate();
            }

            reservationStatusRequestList.add(ReservationStatusRequest.builder()
                    .memberId(reservation.getMember().getId())
                    .createdDate(createdDate)
                    .departureDate(reservation.getDepartureDate())
                    .payment(reservation.getPayment())
                    .reservationStatus(reservation.getReservationStatus())
                    .deletedDate(deleteDate)
                    .build());
        }
        return reservationStatusRequestList;
    }

    public List<BusPopularRoutesRequest> findAllBus() {

        List<Seat> seatList = seatRepository.findAll();
        List<BusPopularRoutesRequest> busPopularRoutesRequestList = new ArrayList<>();

        for (Seat seat : seatList) {
            LocalDate nowDate = LocalDate.now();
            if (seat.getBus().getDepartureDate().isAfter(nowDate)) {
                busPopularRoutesRequestList.add(BusPopularRoutesRequest.builder()
                        .busNum(seat.getBus().getBusNum())
                        .departureDate(seat.getBus().getDepartureDate())
                        .seatNum(seat.getSeatNum())
                        .build());
            }
        }
        return busPopularRoutesRequestList;
    }

    public List<MemberInfoRequest> findAllMember() {

        List<Member> memberList = memberRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
        List<MemberInfoRequest> memberInfoRequestList = new ArrayList<>();

        for (Member member : memberList) {

            LocalDate createdDate = member.getCreatedDate().toLocalDate();
            LocalDate deleteDate = null;

            if(member.getDeletedDate() != null){
                deleteDate = member.getDeletedDate().toLocalDate();
            }

            memberInfoRequestList.add(MemberInfoRequest.builder()
                    .memberId(member.getId())
                    .createdDate(createdDate)
                    .deletedDate(deleteDate)
                    .email(member.getEmail())
                    .grade(member.getGrade())
                    .name(member.getName())
                    .phoneNum(member.getPhoneNum())
                    .role(member.getRole())
                    .build());
        }

        return memberInfoRequestList;
    }

    public List<Long> countReservation(ReservationStatus reservationStatus){
        LocalDate today = LocalDate.now();
        List<Reservation> reservationList = reservationRepository.findByReservationStatus(reservationStatus);

        List<Long> countReservationList = new ArrayList<>();
        Long count = 0L;

        for (int i = 5; i >= 0; i--) {
            for (Reservation reservation: reservationList) {
                System.out.println("reservation.getCreatedDate().toLocalDate()" + reservation.getCreatedDate().toLocalDate());
                System.out.println("today.minusDays(i)" + today.minusDays(i));
                if(reservation.getCreatedDate().toLocalDate().equals(today.minusDays(i)) ){
                    count++;
                }
            }
            System.out.println(count);
            countReservationList.add(count);
            count = 0L;
        }
        return countReservationList;
    }

}
