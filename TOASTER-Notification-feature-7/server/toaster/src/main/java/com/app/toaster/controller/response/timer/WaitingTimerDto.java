package com.app.toaster.controller.response.timer;

import java.time.LocalDateTime;

import com.app.toaster.domain.Reminder;

public record WaitingTimerDto(Long timerId, String remindTime, String remindDates, Boolean isAlarm, LocalDateTime updateAt, String comment, Long categoryId) {
    public static WaitingTimerDto of(Reminder timer, String remindTime, String remindDates) {
        if(timer.getCategory() == null)
            return new WaitingTimerDto(timer.getId(), remindTime, remindDates, timer.getIsAlarm(), timer.getUpdateAt(), "전체", 0L);
        return new WaitingTimerDto(timer.getId(), remindTime, remindDates, timer.getIsAlarm(), timer.getUpdateAt(), timer.getComment(), timer.getCategory().getCategoryId());
    }
}
