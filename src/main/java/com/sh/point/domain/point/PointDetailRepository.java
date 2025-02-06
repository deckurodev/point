package com.sh.point.domain.point;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PointDetailRepository extends JpaRepository<PointDetail, Long> {
	Optional<PointDetail> findTopByUserIdOrderByIdDesc(String userId);

	Optional<PointDetail> findTopByUserIdAndProcessDateBeforeOrderByProcessDateDesc(String userId,
		LocalDateTime processDateMinusOneYear);
}


