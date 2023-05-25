package com.metanet.metabus.board.entity;

import com.metanet.metabus.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "LOST_BOARD")

public class LostBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Integer id;

    private Integer member_id;

    private String title;

    private String content;

    private Integer hit;

    private Timestamp create_date;

    private Timestamp edit_date;

    private Timestamp delete_date;

    private String complete_YN;

    private String filename;//파일이름

    private String filepath;//파일경로

    @PrePersist
    protected void onCreate() {
        if (create_date == null) {
            create_date = new Timestamp(System.currentTimeMillis());
        }
    }

}
