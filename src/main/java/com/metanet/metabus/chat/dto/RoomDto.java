package com.metanet.metabus.chat.dto;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomDto roomDto = (RoomDto) o;
        return Objects.equals(id, roomDto.id) && Objects.equals(name, roomDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
