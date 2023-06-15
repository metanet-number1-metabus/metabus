package com.metanet.metabus.chat.entity;

import com.metanet.metabus.common.BaseEntity;
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
public class Room extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;

    @Column(name = "MEMBER_ID")
    private Long memId;

    private String completeYN;



    public static Room createRoom(String name,Long memId,String completeYN) {
        return Room.builder()
                .name(name)
                .memId(memId)
                .completeYN(completeYN)
                .build();
    }

    public static Room updateRoom(Long id, String name, Long memId,String completeYN) {
        String[] words = name.split(" "); // 공백을 기준으로 문자열을 분리하여 배열로 반환
        String firstWord = words[0];
        return Room.builder()
                .id(id)
                .name(firstWord+" "+"!")
                .memId(memId)
                .completeYN(completeYN)
                .build();
    }

    public static Room updateRoom2(Long id,String name,Long memId,String completeYN) {
        String[] words = name.split(" "); // 공백을 기준으로 문자열을 분리하여 배열로 반환
        String firstWord = words[0];
        return Room.builder()
                .id(id)
                .name(firstWord)
                .memId(memId)
                .completeYN(completeYN)
                .build();
    }

    public static Room completeRoom(Long id,String name,Long memId,String completeYN) {
        String[] words = name.split(" "); // 공백을 기준으로 문자열을 분리하여 배열로 반환
        String firstWord = words[0];
        return Room.builder()
                .id(id)
                .name(firstWord)
                .memId(memId)
                .completeYN(completeYN)
                .build();
    }


}