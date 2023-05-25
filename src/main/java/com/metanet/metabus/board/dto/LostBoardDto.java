package com.metanet.metabus.board.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class LostBoardDto {
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
}
