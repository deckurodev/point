package com.sh.point.domain.point.exception;

import org.springframework.http.HttpStatus;

import com.sh.point.domain.BusinessException;

public class InsufficientBalanceException extends BusinessException {
	public InsufficientBalanceException() {
		super(HttpStatus.UNPROCESSABLE_ENTITY, "포인트 잔액이 부족합니다.");
	}
}
