package com.sh.point.domain.point;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointDetailRepository extends JpaRepository<PointDetail, Long> {
	Optional<PointDetail> findTopByUserIdOrderByIdDesc(String userId);

	// 만료된 포인트의 총합 가져오기
	@Query("""
		    SELECT COALESCE(SUM(pd.amount), 0)
		    FROM PointDetail pd
		    WHERE pd.expireDate < NOW() AND pd.userId = :userId
		""")
	BigDecimal findExpiredPointSum(@Param("userId") String userId);
}


