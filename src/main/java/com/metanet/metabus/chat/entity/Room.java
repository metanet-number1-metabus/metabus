package com.metanet.metabus.chat.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;

    @Column(name = "MEMBER_ID")
    private Long memId;

    @Builder
    public Room(String name) {
        this.name = name;
    }

    /**
     * 채팅방 생성
     *
     * @param name 방 이름
     * @return Room Entity
     */
    public static Room createRoom(String name) {
        return Room.builder()
                .name(name)
                .build();
    }

}