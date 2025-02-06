package com.sh.point.application.lock.aspect;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.sh.point.application.lock.redis.DistributedLock;
import com.sh.point.application.lock.redis.SpELResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLockAspect {
	private static final String REDISSON_LOCK_PREFIX = "LOCK:";

	private final RedissonClient redissonClient;
	private final NewTransactionExecutor newTransactionExecutor;

	@Around("@annotation(com.sh.point.application.lock.redis.DistributedLock)")
	public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

		String key = REDISSON_LOCK_PREFIX + SpELResolver.resolveExpression(distributedLock.key(), joinPoint);
		RLock rLock = redissonClient.getLock(key);

		try {
			boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(),
				TimeUnit.SECONDS);

			if (!available) {
				log.warn("Failed to acquire lock for key: {}", key);
				return false;
			}

			return newTransactionExecutor.proceed(joinPoint);
		} catch (InterruptedException e) {
			throw new InterruptedException();
		} finally {
			try {
				rLock.unlock();
			} catch (IllegalMonitorStateException e) {
				log.error("Unlock Failed", e);
			}
		}
	}
}
