package com.corbanmultibancos.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.UserCreateDTO;
import com.corbanmultibancos.business.dto.UserDataDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.entities.Role;
import com.corbanmultibancos.business.entities.User;
import com.corbanmultibancos.business.mappers.UserMapper;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.repositories.RoleRepository;
import com.corbanmultibancos.business.repositories.UserRepository;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;
import com.corbanmultibancos.business.util.ErrorMessage;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserCsvExporterService exporterService;

	@Transactional(readOnly = true)
	public UserDataDTO getUserById(Long id) {
		Optional<User> result = userRepository.findById(id);
		User user = result.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));
		return UserMapper.toUserDataDto(user);
	}

	@Transactional(readOnly = true)
	public Page<UserDataDTO> getUsers(String username, Pageable pageable) {
		Page<User> userPage;
		if (!username.isBlank()) {
			userPage = getUserByUsername(username);
		}
		else {
			userPage = userRepository.findAll(pageable);
		}
		return userPage.map(user -> UserMapper.toUserDataDto(user));
	}

	public byte[] getUsersAsCsvData(String username) {
		Page<UserDataDTO> result = getUsers(username, Pageable.unpaged());
		return exporterService.writeUsersAsBytes(result.getContent());
	}

	@Transactional
	public UserDataDTO createUser(UserCreateDTO userDto) {
		User user = new User();
		UserMapper.copyUserCreateDtoToEntity(userDto, user);
		setEmployeeAndRole(user, userDto.getEmployeeId(), userDto.getRoleId());
		user = userRepository.save(user);
		return UserMapper.toUserDataDto(user);
	}

	@Transactional
	public UserDataDTO updateUser(Long id, UserCreateDTO userDto) {
		try {
			User user = userRepository.getReferenceById(id);
			UserMapper.copyUserCreateDtoToEntity(userDto, user);
			setEmployeeAndRole(user, id, userDto.getRoleId());
			user = userRepository.save(user);
			return UserMapper.toUserDataDto(user);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND);
		}
	}

	@Transactional
	public void deleteUser(Long id) {
		if(!userRepository.existsById(id)) {
			throw new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND);
		}
		//remover associação antes de deletar
		Employee employee = employeeRepository.findById(id).get();
		employee.setUser(null);
		userRepository.deleteById(id);
	}

	private Page<User> getUserByUsername(String username) {
		Optional<User> result = userRepository.findByUsername(username);
		if (result.isPresent()) {
			return new PageImpl<>(List.of(result.get()));
		}
		return Page.empty();
	}

	private void setEmployeeAndRole(User user, Long employeeId, Long roleId) {
		try {
			Employee employee = employeeRepository.getReferenceById(employeeId);
			user.setEmployee(employee);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(ErrorMessage.EMPLOYEE_NOT_FOUND);
		}
		
		try {
			Role role = roleRepository.getReferenceById(roleId);
			user.setRole(role);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(ErrorMessage.ROLE_NOT_FOUND);
		}
	}
}
