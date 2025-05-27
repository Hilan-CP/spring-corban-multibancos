package com.corbanmultibancos.business.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.corbanmultibancos.business.dto.UserCreateDTO;
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
		if(isIdUnavailable(userDto)) {
			errors.put("employeeId", "O funcionário informado já possui usuário cadastrado");
		}
		if(isUsernameUnavailable(userDto)) {
			errors.put("username", "O nome de usuário informado já está sendo usado por outro usuário");
		}
		context.disableDefaultConstraintViolation();
		errors.forEach((field, message) -> context.buildConstraintViolationWithTemplate(message).addPropertyNode(field).addConstraintViolation());
		return errors.isEmpty();
	}

	private boolean isIdUnavailable(UserDTO userDto) {
		if (userDto instanceof UserCreateDTO) {
			UserCreateDTO userCreateDto = (UserCreateDTO) userDto;
			Long employeeId = userCreateDto.getEmployeeId();
			if(employeeId != null && repository.existsById(employeeId)) {
				return true;
			}
		}
		return false;
	}

	private boolean isUsernameUnavailable(UserDTO userDto) {
		Long userId = getIdUriVariable();
		Optional<User> user = repository.findByUsername(userDto.getUsername());
		return user.isPresent() && user.get().getId() != userId;
	}

	private Long getIdUriVariable() {
		Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		return Long.parseLong(uriVariables.getOrDefault("id", "0"));
	}
}
