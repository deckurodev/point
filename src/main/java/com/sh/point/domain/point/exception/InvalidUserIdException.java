package com.sh.point.domain.point.exception;

import org.springframework.http.HttpStatus;

import com.sh.point.domain.BusinessException;

public class InvalidUserIdException extends BusinessException {

	public InvalidUserIdException(String userId) {
		super(HttpStatus.BAD_REQUEST, "유저 아이디가 잘못 되었습니다. 입력 userId : %s".formatted(userId));
	}

	public InvalidUserIdException() {
		super(HttpStatus.BAD_REQUEST, "유저 아이디가 누락 되었습니다.");
	}
}
