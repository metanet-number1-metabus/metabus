package com.metanet.metabus.board.entity;

import com.metanet.metabus.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "LOST_BOARD")

public class LostBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    private Long memberId;

    private String title;

    private String content;

    private String filename;//파일이름

    private String filepath;//파일경로

    private String departure;

    private String distination;


    public LostBoard(String title, String content, String filename, String filepath, LocalDateTime now,Long memberId, String departure, String distination) {
        super();
        this.title = title;
        this.content = content;
        this.filename = filename;
        this.filepath = filepath;
        this.memberId = memberId;
        this.departure=departure;
        this.distination=distination;

    }

    public LostBoard(Long id,String title, String content, String filename, String filepath) {
        super();
        this.id = id;
        this.title = title;
        this.content = content;
        this.filename = filename;
        this.filepath = filepath;
    }

    public LostBoard(Long id,String title, String content, String filename, String filepath,LocalDateTime deletedDate,Long memberId) {
        super();
        this.id = id;
        this.title = title;
        this.content = content;
        this.filename = filename;
        this.filepath = filepath;
        this.delete();
        this.memberId= memberId;
    }


}
