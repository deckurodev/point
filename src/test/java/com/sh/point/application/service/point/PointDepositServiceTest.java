package com.sh.point.application.service.point;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sh.point.application.service.point.dto.DepositPointServiceRequest;
import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;

@ExtendWith(MockitoExtension.class)
class PointDepositServiceTest {
	private final String testUserId = "testUser";
	private final BigDecimal depositAmount = new BigDecimal("500");
	private final LocalDateTime mockTime = LocalDateTime.of(2025, 2, 5, 12, 0);

	@InjectMocks
	private PointDepositService pointDepositService;

	@Mock
	private PointDetailRepository pointDetailRepository;

	@Mock
	private TimeProvider timeProvider;

	@BeforeEach
	void setUp() {
		when(timeProvider.now()).thenReturn(mockTime);
	}

	@Test
	void 기존_포인트_내역이_존재할_경우_포인트_적립() {
		// given
		PointDetail existingPointDetail = PointDetail.createInitial(testUserId);
		when(pointDetailRepository.findTopByUserIdOrderByIdDesc(testUserId))
			.thenReturn(Optional.of(existingPointDetail));

		DepositPointServiceRequest request = new DepositPointServiceRequest(testUserId, depositAmount);

		// when
		pointDepositService.depositPoints(request);

		// then
		ArgumentCaptor<PointDetail> captor = ArgumentCaptor.forClass(PointDetail.class);
		verify(pointDetailRepository).save(captor.capture());

		PointDetail savedPointDetail = captor.getValue();
		assertThat(savedPointDetail.getDepositSum()).isEqualByComparingTo(depositAmount);
		assertThat(savedPointDetail.getUserId()).isEqualTo(testUserId);
	}

	@Test
	void 기존_포인트_내역이_없을_경우_초기_엔티티_생성_후_포인트_적립() {
		// given
		when(pointDetailRepository.findTopByUserIdOrderByIdDesc(testUserId))
			.thenReturn(Optional.empty());

		DepositPointServiceRequest request = new DepositPointServiceRequest(testUserId, depositAmount);

		// when
		pointDepositService.depositPoints(request);

		// then
		ArgumentCaptor<PointDetail> captor = ArgumentCaptor.forClass(PointDetail.class);
		verify(pointDetailRepository).save(captor.capture());

		PointDetail savedPointDetail = captor.getValue();
		assertThat(savedPointDetail.getDepositSum()).isEqualByComparingTo(depositAmount);
		assertThat(savedPointDetail.getUserId()).isEqualTo(testUserId);
	}
}