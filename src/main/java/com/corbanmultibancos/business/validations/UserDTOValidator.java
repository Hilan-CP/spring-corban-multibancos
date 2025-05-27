package com.corbanmultibancos.business.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.corbanmultibancos.business.dto.UserCreateDTO;
import com.corbanmultibancos.business.dto.UserDTO;
import com.corbanmultibancos.business.entities.User;
import com.corbanmultibancos.business.repositories.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserDTOValidator implements ConstraintValidator<UserDTOValid, UserDTO> {

	@Autowired
	private ValidatorUtil validatorUtil;

	@Autowired
	private UserRepository repository;

	@Override
	public boolean isValid(UserDTO userDto, ConstraintValidatorContext context) {
		Map<String, String> errors = new HashMap<>();
		if (isIdUnavailable(userDto)) {
			errors.put("employeeId", "O funcionário informado já possui usuário cadastrado");
		}
		if (isUsernameUnavailable(userDto)) {
			errors.put("username", "O nome de usuário informado já está sendo usado por outro usuário");
		}
		validatorUtil.buildConstraintViolations(errors, context);
		return errors.isEmpty();
	}

	private boolean isIdUnavailable(UserDTO userDto) {
		if (userDto instanceof UserCreateDTO) {
			UserCreateDTO userCreateDto = (UserCreateDTO) userDto;
			Long employeeId = userCreateDto.getEmployeeId();
			if (employeeId != null && repository.existsById(employeeId)) {
				return true;
			}
		}
		return false;
	}

	private boolean isUsernameUnavailable(UserDTO userDto) {
		Long userId = validatorUtil.getIdPathVariable();
		Optional<User> user = repository.findByUsername(userDto.getUsername());
		return user.isPresent() && user.get().getId() != userId;
	}
}
