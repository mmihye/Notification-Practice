package com.app.toaster.external.client.fcm;

import static com.app.toaster.external.client.fcm.FCMService.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.exception.Success;
import com.app.toaster.external.client.sqs.SqsProducer;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class FCMController {

    private final FCMService fcmService;
//    private final FCMScheduler fcmScheduler;
    private final SqsProducer sqsProducer;

    /**
     * 헤더와 바디를 직접 만들어 알림을 전송하는 테스트용 API
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> sendNotificationByToken(
            @RequestBody FCMPushRequestDto request) throws IOException {

        fcmService.pushAlarm(request);
        return ResponseEntity.ok().body("푸시알림 전송에 성공했습니다!");
    }


    @DeleteMapping("/clear")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse deleteScheduledTasks(){
        clearScheduledTasks();
        return ApiResponse.success(Success.CLEAR_SCHEDULED_TASKS_SUCCESS);
    }
}