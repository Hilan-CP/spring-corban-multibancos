package com.corbanmultibancos.business.validations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BankDTOValidator.class)
public @interface BankDTOValid {
	
	String message() default "Dados inválidos";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
