package com.app.toaster.infrastructure;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.toaster.domain.Category;
import com.app.toaster.domain.Toast;
import com.app.toaster.domain.User;

public interface ToastRepository extends JpaRepository<Toast, Long> {
	@Query("SELECT COUNT(t) FROM Toast t WHERE t.user.userId = :userId AND t.isRead = false")
	Integer getUnReadToastNumber(Long userId);
}
