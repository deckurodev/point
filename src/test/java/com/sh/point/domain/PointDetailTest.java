package com.sh.point.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.sh.point.domain.point.PointDetail;

class PointDetailTest {
	@Test
	void 적립_금액_확인() {
		PointDetail pointDetail = PointDetail.createInitial("1");
		PointDetail deposit = pointDetail.deposit(BigDecimal.valueOf(100), LocalDateTime.now());
		PointDetail deposit1 = deposit.deposit(BigDecimal.valueOf(200), LocalDateTime.now());
		PointDetail deposit2 = deposit1.deposit(BigDecimal.valueOf(300), LocalDateTime.now());
		assertEquals(BigDecimal.valueOf(600), deposit2.getDepositSum());
		assertEquals(BigDecimal.ZERO, deposit2.getWithdrawSum());
		assertEquals(BigDecimal.valueOf(300), deposit2.getAmount());
	}

	@Test
	void 사용_금액_확인() {
		PointDetail pointDetail = PointDetail.createInitial("1");
		PointDetail deposit = pointDetail.deposit(BigDecimal.valueOf(100), LocalDateTime.now());
		PointDetail withdraw = deposit.withdraw(BigDecimal.valueOf(10), BigDecimal.ZERO, LocalDateTime.now());
		PointDetail withdraw2 = withdraw.withdraw(BigDecimal.valueOf(10), BigDecimal.ZERO, LocalDateTime.now());

		assertEquals(BigDecimal.valueOf(-20), withdraw2.getWithdrawSum());
		assertEquals(BigDecimal.valueOf(100), withdraw2.getDepositSum());
	}
}