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

    public static RoomDto of(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .memId(room.getMemId())
                .build();
    }
}
