package com.metanet.metabus.board.entity;

import com.metanet.metabus.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "LOST_BOARD")

public class LostBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Integer id;

    private Integer memberId;

    private String title;

    private String content;

    private Integer hit;

    private String completeYN;

    private String filename;//파일이름

    private String filepath;//파일경로




    public LostBoard(String title, String content, String filename, String filepath) {
        super();
        this.title = title;
        this.content = content;
        this.filename = filename;
        this.filepath = filepath;

    }

    public LostBoard(Integer id,String title, String content, String filename, String filepath) {
        super();
        this.id = id;
        this.title = title;
        this.content = content;
        this.filename = filename;
        this.filepath = filepath;
    }


}
