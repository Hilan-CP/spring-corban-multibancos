package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.nio.charset.StandardCharsets;
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

import com.corbanmultibancos.business.dto.UserCreateDTO;
import com.corbanmultibancos.business.dto.UserDataDTO;
import com.corbanmultibancos.business.dto.UserUpdateDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.entities.Role;
import com.corbanmultibancos.business.entities.User;
import com.corbanmultibancos.business.mappers.UserMapper;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.repositories.RoleRepository;
import com.corbanmultibancos.business.repositories.UserRepository;
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

	@Mock
	private RoleRepository roleRepository;

	private Long existingId;
	private Long nonExistingId;
	private String existingUsername;
	private String nonExistingUsername;
	private Long existingRoleId;
	private Long nonExistingRoleId;
	private User userEntity;
	private Employee employee;
	private Role role;
	private Page<User> userPage;
	private Pageable pageable;
	private UserCreateDTO userCreateDto;
	private UserUpdateDTO userUpdateDto;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		existingUsername = "florinda";
		nonExistingUsername = "usuario inexistente";
		existingRoleId = 1L;
		nonExistingRoleId = 100L;
		employee = new Employee(existingId, null, null, null, null);
		role = new Role(1L, "GESTOR");
		userEntity = new User(existingId, existingUsername, "senha123", employee, role);
		userPage = new PageImpl<>(List.of(userEntity));
		pageable = PageRequest.of(0, 10);
		userCreateDto = new UserCreateDTO(9L, "novo", "novo123", 1L);
		userUpdateDto = new UserUpdateDTO("outro_usuario", "4321", 1L);
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
		Mockito.doNothing().when(userRepository).deleteById(existingId);
		Mockito.when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employee));
		Mockito.when(employeeRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(employeeRepository.getReferenceById(existingId)).thenReturn(employee);
		Mockito.when(employeeRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(employeeRepository.save(any())).thenReturn(employee);
		Mockito.when(roleRepository.getReferenceById(existingRoleId)).thenReturn(role);
		Mockito.when(roleRepository.getReferenceById(nonExistingRoleId)).thenThrow(EntityNotFoundException.class);
	}

	@Test
	public void getUserByIdShouldReturnUserDTOWhenExistingId() {
		UserDataDTO userDto = userService.getUserById(existingId);
		Assertions.assertEquals(existingId, userDto.getEmployeeId());
	}

	@Test
	public void getUserByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(nonExistingId));
	}

	@Test
	public void getUsersShouldReturnPageOfSingleUserDTOWhenExistingUsername() {
		Page<UserDataDTO> userDtoPage = userService.getUsers(existingUsername, pageable);
		Assertions.assertEquals(1, userDtoPage.getSize());
		Mockito.verify(userRepository, times(1)).findByUsername(any());
		Mockito.verify(userRepository, never()).findAll(any(Pageable.class));
	}

	@Test
	public void getUsersShouldReturnEmptyPageWhenNonExistingUsername() {
		Page<UserDataDTO> userDtoPage = userService.getUsers(nonExistingUsername, pageable);
		Assertions.assertTrue(userDtoPage.isEmpty());
		Mockito.verify(userRepository, times(1)).findByUsername(any());
		Mockito.verify(userRepository, never()).findAll(any(Pageable.class));
	}

	@Test
	public void getUsersShouldReturnPageOfUserDTOWhenNoParameter() {
		Page<UserDataDTO> userDtoPage = userService.getUsers("", pageable);
		Assertions.assertFalse(userDtoPage.isEmpty());
		Mockito.verify(userRepository, never()).findByUsername(any());
		Mockito.verify(userRepository, times(1)).findAll(any(Pageable.class));
	}

	@Test
	public void createUserShouldReturnUserDTOWhenExistingEmployee() {
		UserDataDTO userDto = userService.createUser(userCreateDto);
		Assertions.assertNotNull(userDto.getEmployeeId());
	}

	@Test
	public void createUserShouldThrowResourceNotFoundExceptionWhenNonExistingEmployee() {
		userCreateDto.setEmployeeId(nonExistingId);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.createUser(userCreateDto));
	}

	@Test
	public void updateUserShouldReturnUserDTOWhenExistingId() {
		UserDataDTO userDto = userService.updateUser(existingId, userUpdateDto);
		Assertions.assertNotNull(userDto.getEmployeeId());
	}

	@Test
	public void updateUserShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> userService.updateUser(nonExistingId, userUpdateDto));
	}

	@Test
	public void deleteUserShouldDoNothingWhenExistingId() {
		Assertions.assertDoesNotThrow(() -> userService.deleteUser(existingId));
		Mockito.verify(userRepository, times(1)).deleteById(any());
	}

	@Test
	public void deleteUserShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(nonExistingId));
		Mockito.verify(userRepository, never()).deleteById(any());
	}

	@Test
	public void getUserAsCsvDataShouldReturnByteArray() {
		Page<UserDataDTO> page = new PageImpl<>(List.of(UserMapper.toUserDataDto(userEntity)));
		UserService serviceSpy = Mockito.spy(userService);
		Mockito.doReturn(page).when(serviceSpy).getUsers("", Pageable.unpaged());
		byte[] csvData = serviceSpy.getUsersAsCsvData("");
		String content = new String(csvData, StandardCharsets.UTF_8);
		Assertions.assertNotNull(csvData);
		Assertions.assertTrue(content.contains("ID_Funcionário;Usuário;ID_Tipo;Tipo_Usuário"));
		Assertions.assertTrue(content.contains(page.getContent().get(0).getEmployeeId().toString()));
		Assertions.assertTrue(content.contains(page.getContent().get(0).getUsername()));
		Assertions.assertTrue(content.contains(page.getContent().get(0).getRole().getId().toString()));
		Assertions.assertTrue(content.contains(page.getContent().get(0).getRole().getAuthority()));
	}
}
