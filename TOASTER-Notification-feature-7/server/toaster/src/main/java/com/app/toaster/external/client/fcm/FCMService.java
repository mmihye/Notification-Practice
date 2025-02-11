package com.app.toaster.external.client.fcm;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import com.app.toaster.domain.Category;
import com.app.toaster.domain.Reminder;
import com.app.toaster.exception.model.NotFoundException;
import com.app.toaster.external.client.sqs.SqsProducer;
import com.app.toaster.infrastructure.TimerRepository;
import com.app.toaster.infrastructure.ToastRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.app.toaster.exception.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {
    private final ObjectMapper objectMapper;  // FCM의 body 형태에 따라 생성한 값을 문자열로 저장하기 위한 Mapper 클래스

    @Value("${fcm.key.path}")
    private String SERVICE_ACCOUNT_JSON;
    @Value("${fcm.api.url}")
    private String FCM_API_URL;
    @Value("${fcm.topic}")
    private String topic;

    private static ScheduledFuture<?> scheduledFuture;
    private final TaskScheduler taskScheduler;
    private final PlatformTransactionManager transactionManager;
    private final SqsProducer sqsProducer;

    /**
     * 단일 기기
     * - Firebase에 메시지를 수신하는 함수 (헤더와 바디 직접 만들기)
     */
    @Async
    public void pushAlarm(FCMPushRequestDto request) throws IOException {

        String message = makeSingleMessage(request);
        sendPushMessage(message);
    }

    // 요청 파라미터를 FCM의 body 형태로 만들어주는 메서드 [단일 기기]
    private String makeSingleMessage(FCMPushRequestDto request) throws JsonProcessingException {

        FCMMessage fcmMessage = FCMMessage.builder()
                .message(FCMMessage.Message.builder()
                        .token(request.getTargetToken())   // 1:1 전송 시 반드시 필요한 대상 토큰 설정
                        .notification(FCMMessage.Notification.builder()
                                .title(request.getTitle())
                                .body(request.getBody())
                                .image(request.getImage())
                                .build())
                        .build()
                ).validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }


    // 실제 파이어베이스 서버로 푸시 메시지를 전송하는 메서드
    private void sendPushMessage(String message) throws IOException {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request httpRequest = new Request.Builder()
                .url(FCM_API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(httpRequest).execute();

        log.info("단일 기기 알림 전송 성공 ! successCount: 1 messages were sent successfully");
        log.info("알림 전송: {}", response.body().string());
    }

    // Firebase에서 Access Token 가져오기
    private String getAccessToken() throws IOException {

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(SERVICE_ACCOUNT_JSON).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        log.info("getAccessToken() - googleCredentials: {} ", googleCredentials.getAccessToken().getTokenValue());

        return googleCredentials.getAccessToken().getTokenValue();
    }

    // 스케줄러에서 예약된 작업을 제거하는 메서드
    public static void clearScheduledTasks() {
        if (scheduledFuture != null) {
            log.info("이전 스케줄링 예약 취소!");
            scheduledFuture.cancel(false);
        }
        log.info("ScheduledFuture: {}", scheduledFuture);
    }

}