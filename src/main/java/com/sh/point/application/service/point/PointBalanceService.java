package com.sh.point.application.service.point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;
import com.sh.point.web.controller.point.dto.balance.PointBalanceResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PointBalanceService {
	private final PointDetailRepository pointDetailRepository;
	private final TimeProvider timeProvider;

	public PointBalanceResponse getAvailableBalance(String userId) {
		LocalDateTime oneYearAgo = timeProvider.now().minusYears(1);

		PointDetail latestDetail = pointDetailRepository.findTopByUserIdOrderByIdDesc(userId)
			.orElseGet(() -> PointDetail.createInitial(userId));

		PointDetail expiredDetail = pointDetailRepository.findTopByUserIdAndProcessDateBeforeOrderByProcessDateDesc(
				userId, oneYearAgo)
			.orElseGet(() -> PointDetail.createInitial(userId));

		// 만료 금액을 제외한다. (1년전 사용 할수 있는 모든 금액은 만료되어야 하므로)
		BigDecimal balance = latestDetail.calculateAvailableBalanceWithOutExpiredAmount()
			.subtract(expiredDetail.getDepositSum());
		return new PointBalanceResponse(userId, balance);
	}
}