package com.sh.point.domain.point.exception;

import org.springframework.http.HttpStatus;

import com.sh.point.domain.BusinessException;

public class PointNotFoundException extends BusinessException {
	public PointNotFoundException(String userId) {
		super(HttpStatus.NOT_FOUND, "사용자 ID '" + userId + "'에 대한 포인트 내역이 없습니다.");
	}
}
