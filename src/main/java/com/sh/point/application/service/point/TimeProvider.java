package com.sh.point.application.service.point;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TimeProvider {

	@PersistenceContext
	private EntityManager entityManager;

	public LocalDateTime getCurrentTime() {
		Timestamp timestamp = (Timestamp)entityManager.createNativeQuery("SELECT NOW()").getSingleResult();
		return timestamp.toLocalDateTime();
	}
}
