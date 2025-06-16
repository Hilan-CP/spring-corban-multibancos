package com.corbanmultibancos.business.validations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CPFValidator.class)
public @interface CPF {
	
	String message() default "CPF inválido, informe os 11 dígitos sem pontuação";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
