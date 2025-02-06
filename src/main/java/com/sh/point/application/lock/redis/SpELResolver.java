package com.sh.point.application.lock.redis;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELResolver {
	public static String resolveExpression(String expression, JoinPoint joinPoint) {
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();

		Object[] args = joinPoint.getArgs();
		String[] parameterNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
		if (parameterNames != null) {
			for (int i = 0; i < parameterNames.length; i++) {
				context.setVariable(parameterNames[i], args[i]);
			}
		}
		return parser.parseExpression(expression).getValue(context, String.class);
	}

}
