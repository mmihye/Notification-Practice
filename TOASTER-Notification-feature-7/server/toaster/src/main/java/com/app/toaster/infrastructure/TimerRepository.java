package com.app.toaster.infrastructure;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.toaster.domain.Category;
import com.app.toaster.domain.Reminder;
import com.app.toaster.domain.User;

@Repository
public interface TimerRepository extends JpaRepository<Reminder, Long> {
    @Query("select coalesce(r.category.title, '전체') from Reminder r where r.id = :id")
    String findCategoryTitleByReminderId(@Param("id") Long reminderId);

    @Query(value = """
        SELECT r.*
        FROM reminder r
        JOIN user u ON r.user_user_id = u.user_id
        WHERE r.remind_dates LIKE CONCAT('%', :day, '%')
          AND r.is_alarm = true
          AND u.fcm_is_allowed = true
          AND r.remind_time = :now
    """, nativeQuery = true)
    List<Reminder> selectNowReminders(
        @Param("day") String day,
        @Param("now") LocalTime now
    );

}