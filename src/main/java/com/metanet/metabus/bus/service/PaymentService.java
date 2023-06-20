package com.metanet.metabus.bus.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.metanet.metabus.bus.dto.PayRequest;
import com.metanet.metabus.bus.dto.ReceiptResponse;
import com.metanet.metabus.bus.entity.Payment;
import com.metanet.metabus.bus.entity.Reservation;
import com.metanet.metabus.bus.entity.Seat;
import com.metanet.metabus.bus.repository.PaymentRepository;
import com.metanet.metabus.bus.repository.ReservationRepository;
import com.metanet.metabus.bus.repository.SeatRepository;
import com.metanet.metabus.common.exception.not_found.MemberNotFoundException;
import com.metanet.metabus.common.exception.not_found.SeatNotFoundException;
import com.metanet.metabus.member.entity.Member;
import com.metanet.metabus.member.repository.MemberRepository;
import com.metanet.metabus.mileage.entity.Mileage;
import com.metanet.metabus.mileage.entity.SaveStatus;
import com.metanet.metabus.mileage.repository.MileageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Component
@RequiredArgsConstructor
public class PaymentService {

    //---------------------환불, 결제 토큰생성
    @Value("${imp_key}")
    private String impKey;

    @Value("${imp_secret}")
    private String impSecret;

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final MileageRepository mileageRepository;

    public String getToken() throws Exception {

        HttpsURLConnection conn;
        URL url = new URL("https://api.iamport.kr/users/getToken");

        conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        JsonObject json = new JsonObject();

        json.addProperty("imp_key", impKey);
        json.addProperty("imp_secret", impSecret);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

        bw.write(json.toString());
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

        Gson gson = new Gson();

        String response = gson.fromJson(br.readLine(), Map.class).get("response").toString();


        String token = gson.fromJson(response, Map.class).get("access_token").toString();

        br.close();
        conn.disconnect();

        return token;
    }

    public void paymentCancelApi(String accessToken, String impUid, String amount) throws IOException {
        HttpsURLConnection conn;
        URL url = new URL("https://api.iamport.kr/payments/cancel");

        conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", accessToken);

        conn.setDoOutput(true);

        JsonObject json = new JsonObject();

        json.addProperty("imp_uid", impUid);
        json.addProperty("amount", amount);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

        bw.write(json.toString());
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

        br.close();
        conn.disconnect();
    }

    public void completePayment(PayRequest payRequest) {

        // PaymentRepository
        Long memberId = payRequest.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        String impUid = payRequest.getImpUid();
        Long paymentSum = payRequest.getPayment();
        Long usedMileage = payRequest.getUsedMileage();

        double discountPercentage = 0;
        if (usedMileage != 0) {
            discountPercentage = (double) usedMileage / (paymentSum + usedMileage);
        }
        System.out.println(discountPercentage);

        Payment payment = Payment.builder()
                .applyNum(payRequest.getApplyNum())
                .member(member)
                .cardName(payRequest.getCardName())
                .cardNum(payRequest.getCardNum())
                .impUid(impUid)
                .merchantName(payRequest.getMerchantName())
                .payment(paymentSum)
                .payMethod(payRequest.getPayMethod())
                .usedMileage(usedMileage)
                .build();

        paymentRepository.save(payment);

        // ReservationRepository
        String reservationIds = payRequest.getReservationIds();
        String[] strReservationIds = reservationIds.split(",");

        List<Long> longReservationIds = new ArrayList<>();
        for (String strReservationId : strReservationIds) {
            longReservationIds.add(Long.parseLong(strReservationId.trim()));
        }

        int reservationCount = longReservationIds.size();

        for (int i = 0; i < reservationCount; i++) {
            Long reservationId = longReservationIds.get(i);

            Reservation reservation = reservationRepository.findByIdAndDeletedDateIsNull(reservationId);
            reservation.updatePaymentStatus(impUid);

            Long reservationPayment = reservation.getPayment();

            if (usedMileage != 0) {
                double each = reservationPayment * discountPercentage;
                if (i == reservationCount - 1) {
                    reservation.updateUsedMileage((long) (each + usedMileage % reservationCount));
                } else {
                    reservation.updateUsedMileage((long) each);
                }
            }

            reservation.updatePayment(reservationPayment - reservation.getUsedMileage());

            reservationRepository.save(reservation);
        }

        if (usedMileage != 0) {
            Mileage mileage = Mileage.builder()
                    .member(member)
                    .point(usedMileage)
                    .saveStatus(SaveStatus.DOWN)
                    .build();

            mileageRepository.save(mileage);

        }
    }

    public void paymentCancelDb(String reservationIds) {

        String[] strReservationIds = reservationIds.split(",");

        for (String strReservationId : strReservationIds) {
            long reservationId = Long.parseLong(strReservationId.trim());
            Reservation reservation = reservationRepository.findByIdAndDeletedDateIsNull(reservationId);

            Member member = reservation.getMember();

            Seat seat = seatRepository.findById(reservation.getSeatId().getId()).orElseThrow(SeatNotFoundException::new);
            seat.delete();
            seatRepository.save(seat);

            Long usedMileage = reservation.getUsedMileage();
            Mileage mileage = Mileage.builder()
                    .member(member)
                    .point(usedMileage)
                    .saveStatus(SaveStatus.CANCEL)
                    .build();

            mileageRepository.save(mileage);

            reservation.delete();
            reservationRepository.save(reservation);
        }

    }

    public String getImpUid(Long[] reservationIds) {

        Reservation reservation = reservationRepository.findByIdAndDeletedDateIsNull(reservationIds[0]);
        return reservation.getImpUid();
    }

    public ReceiptResponse makeReceipt(String impUid) {
        Payment payment = paymentRepository.findByImpUidAndDeletedDateIsNull(impUid);
        Member member = payment.getMember();

        return ReceiptResponse.builder()
                .applyNum(payment.getApplyNum())
                .memberEmail(member.getEmail())
                .memberName(member.getName())
                .memberPhoneNum(member.getPhoneNum())
                .cardName(payment.getCardName())
                .cardNum(payment.getCardNum())
                .impUid(impUid)
                .merchantName(payment.getMerchantName())
                .payment(payment.getPayment())
                .payMethod(payment.getPayMethod())
                .usedMileage(payment.getUsedMileage())
                .build();
    }

}
