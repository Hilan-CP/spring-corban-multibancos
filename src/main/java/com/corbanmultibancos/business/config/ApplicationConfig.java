package com.corbanmultibancos.business.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

	@Bean
	Clock getSystemClock() {
		return Clock.systemDefaultZone();
	}
}
