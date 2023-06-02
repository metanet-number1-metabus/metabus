package com.metanet.metabus.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.metanet.metabus.chat.entity.Room;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r from Room r where r.memId = :id")
    List<Room> findUser(Long id);


}
