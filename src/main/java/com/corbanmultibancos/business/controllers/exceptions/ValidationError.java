package com.corbanmultibancos.business.controllers.exceptions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ValidationError extends CustomError {

	Map<String, String> errors = new HashMap<>();

	public ValidationError(Instant timestamp, Integer status, String error, String path) {
		super(timestamp, status, error, path);
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void addError(String field, String message) {
		errors.put(field, message);
	}
}
