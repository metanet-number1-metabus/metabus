package com.metanet.metabus.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.metanet.metabus.chat.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
