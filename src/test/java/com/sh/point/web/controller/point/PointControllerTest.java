package com.sh.point.web.controller.point;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sh.point.application.service.point.PointBalanceService;
import com.sh.point.application.service.point.PointDepositService;
import com.sh.point.application.service.point.PointWithdrawService;
import com.sh.point.application.service.point.dto.DepositPointServiceRequest;
import com.sh.point.application.service.point.dto.WithdrawPointServiceRequest;
import com.sh.point.web.controller.point.dto.DepositPointRequest;
import com.sh.point.web.controller.point.dto.balance.PointBalanceResponse;
import com.sh.point.web.controller.point.dto.withdraw.WithdrawPointRequest;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PointController.class)
class PointControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PointWithdrawService pointWithdrawService;

	@MockitoBean
	private PointDepositService pointDepositService;

	@MockitoBean
	private PointBalanceService pointBalanceService;

	@Test
	void 사용() throws Exception {
		// given
		WithdrawPointRequest request = new WithdrawPointRequest("testUser", new BigDecimal("500"));
		String requestJson = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/point/withdraw")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isOk());

		verify(pointWithdrawService, times(1)).withdrawPoints(any(WithdrawPointServiceRequest.class));
	}

	@Test
	void 적립() throws Exception {
		// given
		DepositPointRequest request = new DepositPointRequest("testUser", new BigDecimal("1000"));
		String requestJson = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/point/deposit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isOk());

		verify(pointDepositService, times(1)).depositPoints(any(DepositPointServiceRequest.class));
	}

	@Test
	void 잔액_조회() throws Exception {
		// given
		String userId = "testUser";
		PointBalanceResponse response = new PointBalanceResponse(userId, new java.math.BigDecimal("800"));

		when(pointBalanceService.getAvailableBalance(userId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/point/balance")
				.param("userId", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(userId))
			.andExpect(jsonPath("$.availableBalance").value(800));

		verify(pointBalanceService, times(1)).getAvailableBalance(userId);
	}
}