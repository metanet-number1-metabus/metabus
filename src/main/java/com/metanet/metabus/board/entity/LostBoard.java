package com.metanet.metabus.board.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    private Timestamp createDate;

    private Timestamp editDate;

    private Timestamp deleteDate;

    private String complete_YN;

    private String filename;//파일이름

    private String filepath;//파일경로

    @PrePersist
    protected void onCreate() {
        if (createDate == null) {
            createDate = new Timestamp(System.currentTimeMillis());
        }
    }

    public LostBoard(String title, String content, String filename, String filepath) {
        super();
        this.title = title;
        this.content = content;
        this.filename = filename;
        this.filepath = filepath;
    }


}
