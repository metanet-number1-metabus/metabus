package com.metanet.metabus.board.dto;

import com.metanet.metabus.board.entity.LostBoard;
import lombok.*;

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
    private LocalDateTime createDate;
    private LocalDateTime editDate;
    private LocalDateTime deleteDate;
    private String filename;//파일이름
    private String filepath;//파일경로

    private String departure;
    private String distination;

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
                .createDate(lostBoard.getCreatedDate())
                .editDate(lostBoard.getLastModifiedDate())
                .deleteDate(lostBoard.getDeletedDate())
                .filename(lostBoard.getFilename())
                .filepath(lostBoard.getFilepath())
                .departure(lostBoard.getDeparture())
                .distination(lostBoard.getDistination())
                .build();
    }


}
