package com.app.toaster.controller.request.timer;

import java.util.ArrayList;

public record CreateCronRequestDto(
     Long categoryId,
	 Long timerId,
     String remindTime,
     ArrayList<Integer> remindDates){
}
