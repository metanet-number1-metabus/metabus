package com.metanet.metabus.board.repository;

import com.metanet.metabus.board.entity.LostBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<LostBoard,Integer> {

    Page<LostBoard> findByTitleContaining(String searchKeyword, Pageable pageable);
}
