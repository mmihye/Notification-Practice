// package com.app.toaster.controller.response.timer;
//
// import java.util.ArrayList;
//
// import com.app.toaster.domain.Reminder;
//
// public record GetTimerResponseDto (String categoryName,
//                                        String remindTime,
//                                        ArrayList<Integer> remindDates) {
//     public static GetTimerResponseDto of(Reminder reminder){
//         if(reminder.getCategory() == null)
//             return new GetTimerResponseDto("전체", reminder.getRemindTime().toString(), reminder.getRemindDates());
//         return new GetTimerResponseDto(reminder.getCategory().getTitle(), reminder.getRemindTime().toString(), reminder.getRemindDates());
//     }
// }
