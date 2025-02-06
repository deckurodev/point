package com.sh.point.application.service.point;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class TimeProvider {
	public LocalDateTime now() {
		return LocalDateTime.now();
	}
}
