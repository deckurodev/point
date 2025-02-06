package com.sh.point.web.controller.point;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sh.point.application.service.point.PointBalanceService;
import com.sh.point.application.service.point.PointDepositService;
import com.sh.point.application.service.point.PointWithdrawService;
import com.sh.point.application.service.point.dto.DepositPointServiceRequest;
import com.sh.point.application.service.point.dto.WithdrawPointServiceRequest;
import com.sh.point.web.controller.point.dto.DepositPointRequest;
import com.sh.point.web.controller.point.dto.balance.PointBalanceRequest;
import com.sh.point.web.controller.point.dto.balance.PointBalanceResponse;
import com.sh.point.web.controller.point.dto.withdraw.WithdrawPointRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/point")
public class PointController {

	private final PointWithdrawService pointWithdrawService;
	private final PointDepositService pointDepositService;
	private final PointBalanceService pointBalanceService;

	@PostMapping("/withdraw")
	public void withdraw(@RequestBody WithdrawPointRequest withdrawPointRequest) {
		pointWithdrawService.withdrawPoints(WithdrawPointServiceRequest.of(withdrawPointRequest));
	}

	@PostMapping("/deposit")
	public void deposit(@RequestBody DepositPointRequest depositPointRequest) {
		pointDepositService.depositPoints(DepositPointServiceRequest.of(depositPointRequest));
	}

	@GetMapping("/balance")
	public PointBalanceResponse balance(PointBalanceRequest balanceRequest) {
		PointBalanceResponse pointBalanceResponse = pointBalanceService.getAvailableBalance(balanceRequest.userId());
		return new PointBalanceResponse(pointBalanceResponse.userId(), pointBalanceResponse.availableBalance());
	}
}
