package com.metanet.metabus.board.dto;

import com.metanet.metabus.board.entity.LostBoard;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LostBoardDto {
    private Long id;
    private Long memberId;
    private String title;
    private String content;
    private Long hit;
    private LocalDateTime createDate;
    private LocalDateTime editDate;
    private LocalDateTime deleteDate;
    private String completeYN;
    private String filename;//파일이름
    private String filepath;//파일경로

    public LostBoardDto(Long id,String title,String content,LocalDateTime createDate){
        super();
        this.id = id;
        this.title = title;
        this.content = content;
        this.createDate = createDate;
    }

    public static LostBoardDto of(LostBoard lostBoard){
        return LostBoardDto.builder()
                .id(lostBoard.getId())
                .memberId(lostBoard.getMemberId())
                .title(lostBoard.getTitle())
                .content(lostBoard.getContent())
                .hit(lostBoard.getHit())
                .createDate(lostBoard.getCreatedDate())
                .editDate(lostBoard.getLastModifiedDate())
                .deleteDate(lostBoard.getDeletedDate())
                .completeYN(lostBoard.getCompleteYN())
                .filename(lostBoard.getFilename())
                .filepath(lostBoard.getFilepath())
                .build();
    }
}
