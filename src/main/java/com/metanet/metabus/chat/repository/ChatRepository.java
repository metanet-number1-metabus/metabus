package com.metanet.metabus.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.metanet.metabus.chat.entity.Chat;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByRoomId(Long roomId);
}
