package com.app.toaster.external.client.fcm;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Random;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.app.toaster.domain.Category;
import com.app.toaster.domain.Reminder;
import com.app.toaster.external.client.sqs.SqsProducer;
import com.app.toaster.infrastructure.TimerRepository;
import com.app.toaster.infrastructure.ToastRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMScheduler {
	private final TimerRepository timerRepository;
	private final ToastRepository toastRepository;
	private final SqsProducer sqsProducer;

	private final int PUSH_MESSAGE_NUMBER = 5;
	@Scheduled(cron = "1 * * * * *", zone = "Asia/Seoul")
	@Async
	public void pushTodayTimer() {
		log.info("리마인드 알람");

		// 오늘 요일
		String today = String.valueOf(LocalDateTime.now().getDayOfWeek().getValue());

		LocalTime now = LocalTime.now();

		//한국 시간대로 변환
		ZoneId koreaTimeZone = ZoneId.of("Asia/Seoul");
		ZonedDateTime koreaTime = now.atDate(ZonedDateTime.now().toLocalDate()).atZone(koreaTimeZone);

		//ZonedDateTime에서 LocalTime 추출
		LocalTime koreaLocalTime = koreaTime.toLocalTime();

		// 현재 알람이 커져있고 설정값이 동일하면 알람 전송
		timerRepository.selectNowReminders(
			today, koreaLocalTime.withSecond(0).withNano(0)
		).forEach(timer -> {
			log.info(timer.getId() + "알람 전송");

			//sqs 푸시
			FCMPushRequestDto request = getPushMessage(timer);

			sqsProducer.sendMessage(request, timer.getId().toString());
		});
	}

	private FCMPushRequestDto getPushMessage(Reminder reminder) {
		Random random = new Random();
		int randomNumber = random.nextInt(PUSH_MESSAGE_NUMBER);

		String categoryTitle = timerRepository.findCategoryTitleByReminderId(reminder.getId());
		String userNickname = reminder.getUser().getNickname();

		String title = "";
		String body = "";

		switch (randomNumber) {
			case 0 -> {
				title = userNickname + PushMessage.ALARM_MESSAGE_0.getTitle();
				body = categoryTitle + PushMessage.ALARM_MESSAGE_0.getBody();
			}
			case 1 -> {
				title = "띵동! " + categoryTitle + PushMessage.ALARM_MESSAGE_1.getTitle();
				body = PushMessage.ALARM_MESSAGE_1.getBody();
			}
			case 2 -> {
				title =
					userNickname + "님, " + categoryTitle + PushMessage.ALARM_MESSAGE_2.getTitle();
				body = PushMessage.ALARM_MESSAGE_2.getBody();
			}
			case 3 -> {
				LocalDateTime now = LocalDateTime.now();

				title =
					now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA) + "요일 " + now.getHour() + "시에는 "
						+ categoryTitle + PushMessage.ALARM_MESSAGE_3.getTitle();
				body = PushMessage.ALARM_MESSAGE_3.getBody();
			}
			case 4 -> {
				int unReadToastNumber = toastRepository.getUnReadToastNumber(reminder.getUser().getUserId());

				title =
					userNickname + "님, " + categoryTitle + PushMessage.ALARM_MESSAGE_4.getTitle();
				body = PushMessage.ALARM_MESSAGE_4.getBody() + unReadToastNumber + "개 남아있어요";
			}
		}

		return FCMPushRequestDto.builder()
			.targetToken(reminder.getUser().getFcmToken())
			.title(title)
			.body(body)
			.build();
	}

}
