package com.corbanmultibancos.business.config;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ClockConfig {

	@Bean
	@Primary
	Clock fixedClock() {
		return Clock.fixed(LocalDate.of(2025, 6, 21).atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
	}
}
