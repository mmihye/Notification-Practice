package com.app.toaster.domain;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
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
public class Category{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long categoryId;

	private String title;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	private int priority;

	@OneToOne(cascade = CascadeType.REMOVE)
	private Reminder reminder;

	private LocalDateTime latestReadTime;

	@Builder
	public Category(String title, User user, int priority) {
		this.title = title;
		this.user = user;
		this.priority = priority;
	}

	public int getPriority(){
		return priority;
	}

	public void updateCategoryName(String newTitle){ this.title = newTitle;}

	public void updateCategoryPriority(int newPriority){
		this.priority = newPriority;}

	public void updateLatestReadTime(LocalDateTime now){
		this.latestReadTime = now;
	}
}

