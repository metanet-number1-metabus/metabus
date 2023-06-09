package com.metanet.metabus.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.metanet.metabus.chat.entity.Room;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.memId = :id AND r.deletedDate IS NULL")
    List<Room> findUser(Long id);


    List<Room> findAllByDeletedDateIsNull();
}
