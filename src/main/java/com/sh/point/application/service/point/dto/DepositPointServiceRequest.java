package com.sh.point.application.service.point.dto;

import java.math.BigDecimal;

import com.sh.point.web.controller.point.dto.DepositPointRequest;

public record DepositPointServiceRequest(
	String userId, BigDecimal amount
) {
	public static DepositPointServiceRequest of(DepositPointRequest depositPointRequest) {
		return new DepositPointServiceRequest(depositPointRequest.userId(), depositPointRequest.amount());
	}
}
