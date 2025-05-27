package com.corbanmultibancos.business.validations;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class ValidatorUtil {

	@Autowired
	private HttpServletRequest request;

	@SuppressWarnings("unchecked")
	public Long getIdPathVariable() {
		Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		return Long.parseLong(uriVariables.getOrDefault("id", "0"));
	}

	public void buildConstraintViolations(Map<String, String> errors, ConstraintValidatorContext context) {
		if (!errors.isEmpty()) {
			context.disableDefaultConstraintViolation();
			errors.forEach((field, message) -> context.buildConstraintViolationWithTemplate(message)
													.addPropertyNode(field)
													.addConstraintViolation());
		}
	}
}
