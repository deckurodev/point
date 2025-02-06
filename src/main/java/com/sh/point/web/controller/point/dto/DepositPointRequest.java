package com.sh.point.web.controller.point.dto;

import java.math.BigDecimal;

public record DepositPointRequest(String userId, BigDecimal amount) {
}
