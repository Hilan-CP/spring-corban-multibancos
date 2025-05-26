package com.corbanmultibancos.business.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.corbanmultibancos.business.dto.UserDTO;
import com.corbanmultibancos.business.entities.User;
import com.corbanmultibancos.business.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserDTOValidator implements ConstraintValidator<UserDTOValid, UserDTO> {
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private UserRepository repository;

	@Override
	public boolean isValid(UserDTO userDto, ConstraintValidatorContext context) {
		Map<String, String> errors = new HashMap<>();
		if(isUsernameUnavailable(userDto)) {
			errors.put("username", "O nome de usuário informado já está sendo usado por outro usuário");
		}
		if(!isPasswordValid(userDto)) {
			errors.put("password", "Obrigatório informar a senha");
		}
		context.disableDefaultConstraintViolation();
		errors.forEach((field, message) -> context.buildConstraintViolationWithTemplate(message).addPropertyNode(field).addConstraintViolation());
		return errors.isEmpty();
	}

	private boolean isUsernameUnavailable(UserDTO userDto) {
		Long userId = getIdUriVariable();
		Optional<User> user = repository.findByUsername(userDto.getUsername());
		return user.isPresent() && user.get().getId() != userId;
	}
	
	private boolean isPasswordValid(UserDTO userDto) {
		Long userId = getIdUriVariable();
		Optional<User> user = repository.findById(userId);
		if(user.isEmpty()) {
			if(userDto.getPassword() == null || userDto.getPassword().isBlank()) {
				return false;
			}
		}
		return true;
	}
	
	private Long getIdUriVariable() {
		Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		return Long.parseLong(uriVariables.getOrDefault("id", "0"));
	}
}
