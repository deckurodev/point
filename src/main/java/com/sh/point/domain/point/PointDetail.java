package com.sh.point.domain.point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sh.point.domain.BaseEntity;
import com.sh.point.domain.point.exception.InsufficientBalanceException;
import com.sh.point.domain.point.exception.InvalidAmountException;
import com.sh.point.domain.point.exception.InvalidUserIdException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PointTransactionType pointTransactionType;

	@Column(nullable = false, length = 50)
	private String userId;

	@Column(nullable = false)
	private LocalDateTime processDate;

	@Column(nullable = false)
	private LocalDateTime expireDate;

	// 여태까지 쌓아온 포인트의 합
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal depositSum = BigDecimal.ZERO;

	// 여태까지 사용한 포인트의 합
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal withdrawSum = BigDecimal.ZERO;

	private PointDetail(String userId) {
		setUserId(userId);
		this.amount = BigDecimal.ZERO;
		this.processDate = LocalDateTime.now();
		this.expireDate = processDate.plusYears(1);
	}

	private PointDetail(
		BigDecimal amount,
		PointTransactionType pointTransactionType,
		String userId,
		LocalDateTime processDate,
		LocalDateTime expireDate,
		BigDecimal depositSum,
		BigDecimal withdrawSum
	) {
		this.amount = setAmount(amount);
		this.pointTransactionType = pointTransactionType;
		this.expireDate = expireDate;
		setUserId(userId);
		this.processDate = processDate;
		this.depositSum = setAmount(depositSum);
		this.withdrawSum = setAmount(withdrawSum);
	}

	public static PointDetail createInitial(String userId) {
		return new PointDetail(userId);
	}

	/**
	 * 사용 가능한 잔액 계산 (만료 금액은 포함하지는 않는다.)
	 * @return 만료 금액 계산을 제외한 사용 가능한 잔액
	 */
	public BigDecimal calculateAvailableBalanceWithOutExpiredAmount() {
		return this.depositSum.add(this.withdrawSum);
	}

	public PointDetail withdraw(BigDecimal amount, BigDecimal expiredAmount, LocalDateTime processDate) {
		BigDecimal availableBalance = this.calculateAvailableBalanceWithOutExpiredAmount().subtract(expiredAmount);

		if (availableBalance.compareTo(amount) < 0) {
			throw new InsufficientBalanceException();
		}

		return new PointDetail(
			amount.negate(),
			PointTransactionType.POINT_USE,
			this.userId,
			processDate,
			processDate.plusYears(1),
			this.depositSum,
			this.withdrawSum.subtract(amount)
		);
	}

	public PointDetail deposit(BigDecimal amount, LocalDateTime processDate) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidAmountException();
		}

		return new PointDetail(
			amount,
			PointTransactionType.POINT_ACCUMULATE,
			this.userId,
			processDate,
			processDate.plusYears(1),
			this.depositSum.add(amount),
			this.withdrawSum
		);
	}

	private void setUserId(String userId) {
		if (userId == null) {
			throw new InvalidUserIdException();
		}
		this.userId = userId;
	}

	private BigDecimal setAmount(BigDecimal amount) {
		return amount == null ? BigDecimal.ZERO : amount;
	}
}
