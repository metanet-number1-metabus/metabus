package com.metanet.metabus.chat.dto;

import lombok.*;
import com.metanet.metabus.chat.entity.Room;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomDto {
    private Long id;
    private String name;
    private Long memId;
    private Long cnt;
    private String completeYN;

    public static RoomDto of(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .memId(room.getMemId())
                .completeYN(room.getCompleteYN())
                .build();
    }
}
