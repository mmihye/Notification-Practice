package com.app.toaster.controller.request.timer;

import java.util.ArrayList;

public record UpdateCronDateTimeDto(Long timerId, String remindTime, ArrayList<Integer> remindDates) {
}
