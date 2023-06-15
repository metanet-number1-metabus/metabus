package com.metanet.metabus.chat.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Column(name = "SENDER")
    private String sender;


    @Column(columnDefinition = "CLOB", name = "MESSAGE")
    private String message;

    @CreatedDate
    @Column(updatable = false, name = "CREAT_DATE")
    private LocalDateTime sendDate;

    @Builder
    public Chat(Room room, String sender, String message) {
        this.room = room;
        this.sender = sender;
        this.message = message;
        this.sendDate = LocalDateTime.now();
    }

}
