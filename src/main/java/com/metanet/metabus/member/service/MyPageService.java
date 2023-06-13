package com.metanet.metabus.member.service;

import com.metanet.metabus.member.repository.MyPageRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class MyPageService {
    private final MyPageRepository myPageRepository;

    public MyPageService(MyPageRepository myPageRepository) {
        this.myPageRepository = myPageRepository;
    }

    public Long selectTickets(Long id) {
        Long tickets = myPageRepository.myTickets(id);
        return tickets;
    }

    public Timestamp selectDates(Long id) {
        Timestamp dates = myPageRepository.myDates(id);
        return dates;
    }

    public String selectGrade(Long id) {
        String grade = myPageRepository.myGrede(id);
        return grade;
    }

    public Long selectMileage(Long id) {
        Long mileage = myPageRepository.myMileage(id);
        return mileage;
    }
}
