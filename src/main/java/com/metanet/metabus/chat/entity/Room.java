package com.metanet.metabus.chat.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;

    @Column(name = "MEMBER_ID")
    private Long memId;

//    @Builder
//    public Room(String name) {
//        this.name = name;
//    }

    /**
     * 채팅방 생성
     *
     * @param name 방 이름
     * @return Room Entity
     */
    public static Room createRoom(String name,Long memId) {
        return Room.builder()
                .name(name)
                .memId(memId)
                .build();
    }

    public static Room updateRoom(Long id, String name, Long memId) {
        String[] words = name.split(" "); // 공백을 기준으로 문자열을 분리하여 배열로 반환
        String firstWord = words[0];
        return Room.builder()
                .id(id)
                .name(firstWord+" "+"!")
                .memId(memId)
                .build();
    }

    public static Room updateRoom2(Long id,String name,Long memId) {
        String[] words = name.split(" "); // 공백을 기준으로 문자열을 분리하여 배열로 반환
        String firstWord = words[0];
        return Room.builder()
                .id(id)
                .name(firstWord)
                .memId(memId)
                .build();
    }


}