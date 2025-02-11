package com.sh.point.domain.point;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointType {
	POINT_ACCUMULATE("적립", "포인트 적립"),
	POINT_USE("사용", "포인트 사용"),
	POINT_CANCEL("취소", "포인트 취소"),
	;
	private final String name;
	private final String description;
}
