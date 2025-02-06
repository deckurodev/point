package com.sh.point.application.service.point.dto;

import java.math.BigDecimal;

import com.sh.point.web.controller.point.dto.withdraw.WithdrawPointRequest;

public record WithdrawPointServiceRequest(String userId, BigDecimal amount) {
	public static WithdrawPointServiceRequest of(WithdrawPointRequest withdrawPointRequest) {
		return new WithdrawPointServiceRequest(withdrawPointRequest.userId(), withdrawPointRequest.usePoint());
	}
}
