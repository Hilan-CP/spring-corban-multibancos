package com.corbanmultibancos.business.util;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {

	private Clock clock;

	public DateUtil(Clock clock) {
		this.clock = clock;
	}

	public LocalDate getDateOfCurrentDay() {
		return LocalDate.now(clock);
	}

	public LocalDate getDateOfFirstDayOfMonth() {
		return LocalDate.now(clock).withDayOfMonth(1);
	}

	public LocalDate getDateOfLastDayOfMonth() {
		LocalDate today = LocalDate.now(clock);
		return today.withDayOfMonth(today.lengthOfMonth());
	}
}
