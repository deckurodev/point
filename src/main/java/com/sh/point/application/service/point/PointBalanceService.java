package com.sh.point.application.service.point;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;
import com.sh.point.domain.point.exception.InvalidAmountException;
import com.sh.point.web.controller.point.dto.balance.PointBalanceResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PointBalanceService {
	private final PointDetailRepository pointDetailRepository;

	public PointBalanceResponse getAvailableBalance(String userId) {
		PointDetail latestDetail = pointDetailRepository.findTopByUserIdOrderByIdDesc(userId)
			.orElseGet(() -> PointDetail.createInitial(userId));

		BigDecimal expiredPointSum = pointDetailRepository.findExpiredPointSum(userId);

		// 만료 금액을 제외한다.
		BigDecimal balance = calculateBalance(latestDetail, expiredPointSum);

		return new PointBalanceResponse(userId, balance);
	}

	private static BigDecimal calculateBalance(PointDetail latestDetail, BigDecimal expiredPointSum) {
		BigDecimal balance = latestDetail.calculateAvailableBalanceWithOutExpiredAmount().subtract(expiredPointSum);

		if (balance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidAmountException();
		}

		return balance;
	}
}