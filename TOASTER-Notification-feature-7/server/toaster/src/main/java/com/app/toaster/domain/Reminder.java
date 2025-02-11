package com.app.toaster.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reminder{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	@OneToOne
	private Category category;

	private LocalTime remindTime;

	@Convert(converter = IntegerListConverter.class)
	private ArrayList<Integer> remindDates;

	private String comment;

	private Boolean isAlarm;

	private LocalDateTime updateAt = LocalDateTime.now();

	@Builder
	public Reminder(User user, Category category, String comment, LocalTime remindTime, ArrayList<Integer> remindDates, Boolean isAlarm) {
		this.user = user;
		this.category = category;
		this.comment = comment;
		this.remindDates = remindDates;
		this.remindTime = remindTime;
		this.isAlarm = isAlarm;
	}

	public void updateRemindTime(String remindTime){
		this.remindTime = LocalTime.parse(remindTime);
	}

	public void updateRemindDates(ArrayList<Integer> remindDates){
		this.remindDates = remindDates;
	}

	public void updateComment(String comment){
		this.comment = comment;
	}

	public void changeAlarm(){
		this.isAlarm = !isAlarm;
	}

	public void setUpdatedAtNow(){
		this.updateAt = LocalDateTime.now();
	}

}
