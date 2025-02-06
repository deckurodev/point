package com.sh.point.web.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sh.point.application.service.point.PointDepositService;
import com.sh.point.application.service.point.dto.DepositPointServiceRequest;
import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;

@SpringBootTest
public class PointDepositConcurrencyTest {
	private final String testUserId = "testUser";

	@Autowired
	PointDepositService pointDepositService;

	@Autowired
	private PointDetailRepository pointDetailRepository;

	@Test
	public void 포인트_적립_동시성_테스트() throws InterruptedException {
		int numberOfThreads = 100;
		BigDecimal amountPerRequest = new BigDecimal("100");

		ExecutorService executorService = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					pointDepositService.depositPoints(new DepositPointServiceRequest(testUserId, amountPerRequest));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		PointDetail pointDetail = pointDetailRepository.findTopByUserIdOrderByIdDesc(testUserId).orElseThrow();
		assertThat(pointDetail.getDepositSum()).isEqualByComparingTo(BigDecimal.valueOf(10000));
		assertThat(pointDetail.getWithdrawSum()).isEqualByComparingTo(BigDecimal.ZERO);
	}
}
