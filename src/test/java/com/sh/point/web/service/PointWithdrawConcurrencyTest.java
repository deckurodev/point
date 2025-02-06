package com.sh.point.web.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sh.point.application.service.point.PointWithdrawService;
import com.sh.point.application.service.point.dto.WithdrawPointServiceRequest;
import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;

@SpringBootTest
class PointWithdrawConcurrencyTest {
	private final String testUserId = "testUser";

	@Autowired
	private PointWithdrawService pointWithdrawService;

	@Autowired
	private PointDetailRepository pointDetailRepository;

	@BeforeEach
	void setUp() {
		PointDetail initialPoint = PointDetail.createInitial(testUserId);
		pointDetailRepository.save(initialPoint.deposit(new BigDecimal("10000"), LocalDateTime.now()));
		pointDetailRepository.flush();
	}

	@Test
	void 포인트_사용_동시성_테스트() throws InterruptedException {
		int numberOfThreads = 100;
		BigDecimal amountPerRequest = new BigDecimal("100");

		ExecutorService executorService = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					pointWithdrawService.withdrawPoints(new WithdrawPointServiceRequest(testUserId, amountPerRequest));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		PointDetail finalPointDetail = pointDetailRepository.findTopByUserIdOrderByIdDesc(testUserId).orElseThrow();
		assertThat(finalPointDetail.getDepositSum()).isEqualByComparingTo(BigDecimal.valueOf(10000));
		assertThat(finalPointDetail.getWithdrawSum()).isEqualByComparingTo(BigDecimal.valueOf(-10000));
	}
}