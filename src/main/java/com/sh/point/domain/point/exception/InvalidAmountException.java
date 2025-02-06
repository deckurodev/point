package com.sh.point.domain.point.exception;

import org.springframework.http.HttpStatus;

import com.sh.point.domain.BusinessException;

public class InvalidAmountException extends BusinessException {

	public InvalidAmountException() {
		super(HttpStatus.BAD_REQUEST, "유효하지 않은 금액입니다.");
	}
}
