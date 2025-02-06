package com.sh.point.application.service.point;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;
import com.sh.point.web.controller.point.dto.balance.PointBalanceResponse;

@ExtendWith(MockitoExtension.class)
class PointBalanceServiceTest {

	@Mock
	private PointDetailRepository pointDetailRepository;

	@Mock
	private TimeProvider timeProvider;

	@InjectMocks
	private PointBalanceService pointBalanceService;

	private final String userId = "testUser";
	private final LocalDateTime fixedNow = LocalDateTime.now();
	private final LocalDateTime oneYearAgo = fixedNow.minusYears(1);

	@BeforeEach
	void setUp() {
		when(timeProvider.now()).thenReturn(fixedNow);
	}

	@Test
	void 만료금액이_없고_현재_잔액을_조회할_경우_남은_금액만_반환해야한다() {
		// given
		PointDetail latestDetail = mock(PointDetail.class);
		when(latestDetail.calculateAvailableBalanceWithOutExpiredAmount()).thenReturn(BigDecimal.valueOf(1000));
		when(pointDetailRepository.findTopByUserIdOrderByIdDesc(userId)).thenReturn(Optional.of(latestDetail));
		when(pointDetailRepository.findTopByUserIdAndProcessDateBeforeOrderByProcessDateDesc(userId, oneYearAgo))
			.thenReturn(Optional.empty());

		// when
		PointBalanceResponse response = pointBalanceService.getAvailableBalance(userId);

		// then
		assertEquals(userId, response.userId());
		assertEquals(BigDecimal.valueOf(1000), response.availableBalance());
	}

	@Test
	void 만료금액이_있는경우_만료금액을_제외한_잔액을_반환해야한다() {
		// given
		PointDetail latestDetail = mock(PointDetail.class);
		when(latestDetail.calculateAvailableBalanceWithOutExpiredAmount()).thenReturn(BigDecimal.valueOf(1000));

		PointDetail expireDetail = mock(PointDetail.class);
		when(expireDetail.getDepositSum()).thenReturn(BigDecimal.valueOf(200));

		when(pointDetailRepository.findTopByUserIdOrderByIdDesc(userId)).thenReturn(Optional.of(latestDetail));
		when(pointDetailRepository.findTopByUserIdAndProcessDateBeforeOrderByProcessDateDesc(userId, oneYearAgo))
			.thenReturn(Optional.of(expireDetail));

		// when
		PointBalanceResponse response = pointBalanceService.getAvailableBalance(userId);

		// then
		assertEquals(userId, response.userId());
		assertEquals(BigDecimal.valueOf(800), response.availableBalance());
	}

	@Test
	void 적립_금액이_없는경우_0원을_반환해야한다() {
		// given
		when(pointDetailRepository.findTopByUserIdOrderByIdDesc(userId)).thenReturn(Optional.empty());
		when(pointDetailRepository.findTopByUserIdAndProcessDateBeforeOrderByProcessDateDesc(userId, oneYearAgo))
			.thenReturn(Optional.empty());

		// when
		PointBalanceResponse response = pointBalanceService.getAvailableBalance(userId);

		// then
		assertEquals(userId, response.userId());
		assertEquals(BigDecimal.ZERO, response.availableBalance());
	}
}