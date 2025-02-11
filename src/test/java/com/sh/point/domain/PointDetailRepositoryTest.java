package com.sh.point.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sh.point.RepositoryTest;
import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;

class PointDetailRepositoryTest extends RepositoryTest {
	private final String testUserId = "testUserId";

	@Autowired
	private PointDetailRepository pointDetailRepository;

	@BeforeEach
	void setUp() {
		PointDetail initial = PointDetail.createInitial(testUserId);
		PointDetail deposit1 = initial.deposit(BigDecimal.valueOf(200), LocalDateTime.of(1991, 4, 14, 11, 20, 10));
		PointDetail deposit2 = initial.deposit(BigDecimal.valueOf(300), LocalDateTime.of(1991, 4, 14, 23, 20, 10));
		PointDetail deposit3 = initial.deposit(BigDecimal.valueOf(300), LocalDateTime.of(1992, 4, 15, 23, 20, 10));
		PointDetail deposit4 = initial.deposit(BigDecimal.valueOf(300), LocalDateTime.of(1992, 4, 15, 23, 20, 10));

		pointDetailRepository.saveAllAndFlush(
			List.of(deposit1, deposit2, deposit3, deposit4)
		);
	}

	@Test
	public void expireAmountTest() {
		BigDecimal expiredPointSum = pointDetailRepository.findExpiredPointSum(testUserId);
		Assertions.assertThat(expiredPointSum.stripTrailingZeros())
			.isEqualTo(BigDecimal.valueOf(1100).stripTrailingZeros());
	}
}