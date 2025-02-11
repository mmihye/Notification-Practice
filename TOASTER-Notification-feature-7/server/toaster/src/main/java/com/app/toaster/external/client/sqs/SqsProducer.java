package com.app.toaster.external.client.sqs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.app.toaster.external.client.fcm.FCMPushRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsProducer {

    @Value("${cloud.aws.sqs.notification.name}")
    private String QUEUE_NAME;

    private static final String GROUP_ID = "sqs";
    private final ObjectMapper objectMapper;
    private final SqsTemplate template;
    private static final String SQS_QUEUE_REQUEST_LOG_MESSAGE = "====> [SQS Queue Request] : %s ";


    @Async
    public void sendMessage(FCMPushRequestDto requestDto, String timerId) {
        System.out.println("Sender: " + requestDto.getBody());
        template.send(to -> {
            try {
                to.queue(QUEUE_NAME)
                        .messageGroupId(timerId)
                        .payload(objectMapper.writeValueAsString(requestDto));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}