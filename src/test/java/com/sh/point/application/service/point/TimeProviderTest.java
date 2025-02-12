package com.sh.point.application.service.point;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TimeProviderTest {
	@Autowired
	TimeProvider timeProvider;

	@Test
	public void currentTime() {
		LocalDateTime currentTime = timeProvider.getCurrentTime();
		Assertions.assertNotNull(currentTime);
	}
}