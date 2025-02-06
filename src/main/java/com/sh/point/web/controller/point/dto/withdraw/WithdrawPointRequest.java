package com.sh.point.web.controller.point.dto.withdraw;

import java.math.BigDecimal;

public record WithdrawPointRequest(
	String userId,
	BigDecimal usePoint
) {
}
