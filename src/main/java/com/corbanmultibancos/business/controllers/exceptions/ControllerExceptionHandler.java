package com.corbanmultibancos.business.controllers.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.corbanmultibancos.business.services.exceptions.DataIntegrityException;
import com.corbanmultibancos.business.services.exceptions.ForbiddenException;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<CustomError> handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request){
		HttpStatus status = HttpStatus.NOT_FOUND;
		CustomError error = new CustomError(Instant.now(), status.value(), exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(error);
	}

	@ExceptionHandler(IllegalParameterException.class)
	public ResponseEntity<CustomError> handleIllegalParameterException(IllegalParameterException exception, HttpServletRequest request){
		HttpStatus status = HttpStatus.BAD_REQUEST;
		CustomError error = new CustomError(Instant.now(), status.value(), exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request){
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		ValidationError error = new ValidationError(Instant.now(), status.value(), "Erro de validação", request.getRequestURI());
		for(FieldError fieldError : exception.getFieldErrors()) {
			error.addError(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return ResponseEntity.status(status).body(error);
	}

	@ExceptionHandler(DataIntegrityException.class)
	public ResponseEntity<CustomError> handleDataIntegrityException(DataIntegrityException exception, HttpServletRequest request){
		HttpStatus status = HttpStatus.CONFLICT;
		CustomError error = new CustomError(Instant.now(), status.value(), exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(error);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<Void> handleForbiddenException(ForbiddenException exception, HttpServletRequest request){
		HttpStatus status = HttpStatus.FORBIDDEN;
		return ResponseEntity.status(status).build();
	}
}
