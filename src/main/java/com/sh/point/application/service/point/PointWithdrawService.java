package com.sh.point.application.service.point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sh.point.application.lock.redis.DistributedLock;
import com.sh.point.application.service.point.dto.WithdrawPointServiceRequest;
import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;
import com.sh.point.domain.point.exception.PointNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PointWithdrawService {
	private final PointDetailRepository pointDetailRepository;
	private final TimeProvider timeProvider;

	@DistributedLock(key = "#request.userId")
	public void withdrawPoints(WithdrawPointServiceRequest request) {
		LocalDateTime now = timeProvider.now();
		LocalDateTime oneYearAgo = now.minusYears(1);

		// 현재 포인트 내역 조회
		PointDetail latestDetail = pointDetailRepository.findTopByUserIdOrderByIdDesc(request.userId())
			.orElseThrow(() -> new PointNotFoundException(request.userId()));

		// 만료된 포인트 조회 (만료 날짜 기준 -> 현시점 1년전)
		BigDecimal expiredPointSum = pointDetailRepository.findExpiredPointSum(request.userId());

		// expiredDetail.getDepositSum() : 1년전 적립금액 -> 현재는 만료 되었어야 하는 금액.
		PointDetail usedDetail = latestDetail.withdraw(request.amount(), expiredPointSum, now);
		pointDetailRepository.save(usedDetail);
	}
}
