package com.metanet.metabus.chat.dto;

import com.metanet.metabus.common.BaseEntity;
import lombok.*;
import com.metanet.metabus.chat.entity.Room;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomDto {
    private Long id;
    private String name;
    private Long memId;
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
