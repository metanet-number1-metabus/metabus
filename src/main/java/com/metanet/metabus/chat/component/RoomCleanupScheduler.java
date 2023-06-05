package com.metanet.metabus.chat.component;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RoomCleanupScheduler {
    private final JdbcTemplate jdbcTemplate;

    public RoomCleanupScheduler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void cleanupRooms() {

        String sql = "UPDATE room SET DELETED_DATE = CURRENT_TIMESTAMP WHERE completeYN = '완료'";
        jdbcTemplate.update(sql);
    }
}
