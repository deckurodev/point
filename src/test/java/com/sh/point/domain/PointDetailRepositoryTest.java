package com.sh.point.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sh.point.domain.point.PointDetail;
import com.sh.point.domain.point.PointDetailRepository;

@Transactional
@ActiveProfiles("local")
@DataJpaTest
class PointDetailRepositoryTest {
	@Autowired
	private PointDetailRepository pointDetailRepository;

	@BeforeEach
	void setUp() {
		PointDetail initial = PointDetail.createInitial("123");
		PointDetail deposit1 = initial.deposit(BigDecimal.valueOf(200), LocalDateTime.of(1991, 4, 14, 11, 20, 10));
		PointDetail deposit2 = initial.deposit(BigDecimal.valueOf(300), LocalDateTime.of(1991, 4, 14, 23, 20, 10));
		PointDetail deposit3 = initial.deposit(BigDecimal.valueOf(300), LocalDateTime.of(1992, 4, 15, 23, 20, 10));
		PointDetail deposit4 = initial.deposit(BigDecimal.valueOf(300), LocalDateTime.of(1992, 4, 15, 23, 20, 10));

		pointDetailRepository.saveAllAndFlush(
			List.of(deposit1, deposit2, deposit3, deposit4)
		);
	}
}