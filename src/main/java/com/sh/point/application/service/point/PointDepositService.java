package com.sh.point.application.service.point;

import org.springframework.stereotype.Service;

import com.sh.point.application.lock.redis.DistributedLock;
import com.sh.point.application.service.point.dto.DepositPointServiceRequest;
import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PointDepositService {
	private final PointDetailRepository pointDetailRepository;
	private final TimeProvider timeProvider;

	@DistributedLock(key = "#request.userId")
	public void depositPoints(DepositPointServiceRequest request) {
		PointDetail lastPointDetail = pointDetailRepository.findTopByUserIdOrderByIdDesc(request.userId())
			.orElseGet(() -> PointDetail.createInitial(request.userId()));
		PointDetail deposit = lastPointDetail.deposit(request.amount(), timeProvider.now());
		pointDetailRepository.save(deposit);
	}
}
