package com.sh.point.web.controller.point.dto.balance;

import java.math.BigDecimal;

public record PointBalanceResponse(
	String userId,
	BigDecimal availableBalance
) {
}
