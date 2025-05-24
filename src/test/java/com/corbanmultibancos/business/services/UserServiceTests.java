package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.UserDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.entities.Role;
import com.corbanmultibancos.business.entities.User;
import com.corbanmultibancos.business.mappers.UserMapper;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.repositories.UserRepository;
import com.corbanmultibancos.business.services.exceptions.DataIntegrityException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private EmployeeRepository employeeRepository;

	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private String existingUsername;
	private String nonExistingUsername;
	private User userEntity;
	private Employee employee;
	private Role role;
	private Page<User> userPage;
	private Pageable pageable;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		dependentId = 2L;
		existingUsername = "florinda";
		nonExistingUsername = "usuario inexistente";
		employee = new Employee(existingId, null, null, null, null);
		role = new Role(1L, "GESTOR");
		userEntity = new User(existingId, existingUsername, "senha123", employee, role);
		userPage = new PageImpl<>(List.of(userEntity));
		pageable = PageRequest.of(0, 10);
		Mockito.when(userRepository.findById(existingId)).thenReturn(Optional.of(userEntity));
		Mockito.when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(userEntity));
		Mockito.when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());
		Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
		Mockito.when(userRepository.save(any())).thenReturn(userEntity);
		Mockito.when(userRepository.getReferenceById(existingId)).thenReturn(userEntity);
		Mockito.when(userRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(userRepository.existsById(existingId)).thenReturn(true);
		Mockito.when(userRepository.existsById(nonExistingId)).thenReturn(false);
		Mockito.when(userRepository.existsById(dependentId)).thenReturn(true);
		Mockito.doNothing().when(userRepository).deleteById(existingId);
		Mockito.when(employeeRepository.existsById(dependentId)).thenReturn(true);
	}

	@Test
	public void getUserByIdShouldReturnUserDTOWhenExistingId() {
		UserDTO userDto = userService.getUserById(existingId);
		Assertions.assertEquals(existingId, userDto.getId());
	}

	@Test
	public void getUserByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(nonExistingId));
	}

	@Test
	public void getUsersShouldReturnPageOfSingleUserDTOWhenExistingUsername() {
		Page<UserDTO> userDtoPage = userService.getUsers(existingUsername, pageable);
		Assertions.assertEquals(1, userDtoPage.getSize());
		Mockito.verify(userRepository, times(1)).findByUsername(existingUsername);
		Mockito.verify(userRepository, never()).findAll(pageable);
	}

	@Test
	public void getUsersShouldReturnEmptyPageWhenNonExistingUsername() {
		Page<UserDTO> userDtoPage = userService.getUsers(nonExistingUsername, pageable);
		Assertions.assertTrue(userDtoPage.isEmpty());
		Mockito.verify(userRepository, times(1)).findByUsername(nonExistingUsername);
		Mockito.verify(userRepository, never()).findAll(pageable);
	}

	@Test
	public void getUsersShouldReturnPageOfUserDTOWhenNoParameter() {
		Page<UserDTO> userDtoPage = userService.getUsers(nonExistingUsername, pageable);
		Assertions.assertFalse(userDtoPage.isEmpty());
		Mockito.verify(userRepository, never()).findByUsername(existingUsername);
		Mockito.verify(userRepository, times(1)).findAll(pageable);
	}

	@Test
	public void createUserShouldReturnUserDTO() {
		UserDTO userDto = userService.createUser(UserMapper.toDto(userEntity));
		Assertions.assertNotNull(userDto.getId());
	}

	@Test
	public void updateUserShouldReturnUserDTOWhenExistingId() {
		UserDTO userDto = userService.updateUser(existingId, UserMapper.toDto(userEntity));
		Assertions.assertNotNull(userDto.getId());
	}

	@Test
	public void updateUserShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> userService.updateUser(nonExistingId, UserMapper.toDto(userEntity)));
	}

	@Test
	public void deleteUserShouldDoNothingWhenExistingId() {
		Assertions.assertDoesNotThrow(() -> userService.deleteUser(existingId));
		Mockito.verify(userRepository, times(1)).deleteById(existingId);
	}

	@Test
	public void deleteUserShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> userService.deleteUser(nonExistingId));
		Mockito.verify(userRepository, never()).deleteById(nonExistingId);
	}

	@Test
	public void deleteUserShouldThrowDataIntegrityExceptionWhenDependentId() {
		Assertions.assertThrows(DataIntegrityException.class,
				() -> userService.deleteUser(dependentId));
		Mockito.verify(userRepository, never()).deleteById(dependentId);
	}
}
