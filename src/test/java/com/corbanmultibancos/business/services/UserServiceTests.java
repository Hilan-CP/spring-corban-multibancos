package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private RoleRepository roleRepository;
	
	@Mock
	private UserCsvExporterService exporterService;

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
		userCreateDto = new UserCreateDTO(existingId, "novo", "novo123", 1L);
	}

	@Test
	public void getUserByIdShouldReturnUserDTOWhenExistingId() {
		Mockito.when(userRepository.findById(existingId)).thenReturn(Optional.of(userEntity));
		UserDataDTO userDto = userService.getUserById(existingId);
		Assertions.assertEquals(existingId, userDto.getEmployeeId());
	}

	@Test
	public void getUserByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Mockito.when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(nonExistingId));
	}

	@Test
	public void getUsersShouldReturnPageOfSingleUserDTOWhenExistingUsername() {
		Mockito.when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(userEntity));
		Page<UserDataDTO> userDtoPage = userService.getUsers(existingUsername, pageable);
		Assertions.assertEquals(1, userDtoPage.getSize());
		Mockito.verify(userRepository, times(1)).findByUsername(any());
		Mockito.verify(userRepository, never()).findAll(any(Pageable.class));
	}

	@Test
	public void getUsersShouldReturnEmptyPageWhenNonExistingUsername() {
		Mockito.when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());
		Page<UserDataDTO> userDtoPage = userService.getUsers(nonExistingUsername, pageable);
		Assertions.assertTrue(userDtoPage.isEmpty());
		Mockito.verify(userRepository, times(1)).findByUsername(any());
		Mockito.verify(userRepository, never()).findAll(any(Pageable.class));
	}

	@Test
	public void getUsersShouldReturnPageOfUserDTOWhenNoParameter() {
		Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
		Page<UserDataDTO> userDtoPage = userService.getUsers("", pageable);
		Assertions.assertFalse(userDtoPage.isEmpty());
		Mockito.verify(userRepository, never()).findByUsername(any());
		Mockito.verify(userRepository, times(1)).findAll(any(Pageable.class));
	}

	@Test
	public void createUserShouldReturnUserDTOWhenExistingEmployee() {
		Mockito.when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employee));
		Mockito.when(roleRepository.findById(existingRoleId)).thenReturn(Optional.of(role));
		Mockito.when(userRepository.save(any())).thenReturn(userEntity);
		UserDataDTO userDto = userService.createUser(userCreateDto);
		Assertions.assertNotNull(userDto.getEmployeeId());
	}

	@Test
	public void createUserShouldThrowResourceNotFoundExceptionWhenNonExistingEmployee() {
		userCreateDto.setEmployeeId(nonExistingId);
		Mockito.when(employeeRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.createUser(userCreateDto));
	}

	@Test
	public void createUserShouldThrowResourceNotFoundExceptionWhenNonExistingRole() {
		userCreateDto.setRoleId(nonExistingRoleId);
		Mockito.when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employee));
		Mockito.when(roleRepository.findById(nonExistingRoleId)).thenReturn(Optional.empty());
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.createUser(userCreateDto));
	}

	@Test
	public void updateUserShouldReturnUserDTOWhenExistingId() {
		Mockito.when(userRepository.getReferenceById(existingId)).thenReturn(userEntity);
		Mockito.when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employee));
		Mockito.when(roleRepository.findById(existingRoleId)).thenReturn(Optional.of(role));
		Mockito.when(userRepository.save(any())).thenReturn(userEntity);
		UserDataDTO userDto = userService.updateUser(existingId, userCreateDto);
		Assertions.assertNotNull(userDto.getEmployeeId());
	}

	@Test
	public void updateUserShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Mockito.when(userRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> userService.updateUser(nonExistingId, userCreateDto));
	}

	@Test
	public void deleteUserShouldDoNothingWhenExistingId() {
		Mockito.when(userRepository.existsById(existingId)).thenReturn(true);
		Mockito.when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employee));
		Assertions.assertDoesNotThrow(() -> userService.deleteUser(existingId));
		Mockito.verify(userRepository, times(1)).deleteById(any());
	}

	@Test
	public void deleteUserShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Mockito.when(userRepository.existsById(nonExistingId)).thenReturn(false);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(nonExistingId));
		Mockito.verify(userRepository, never()).deleteById(any());
	}

	@Test
	public void getUserAsCsvDataShouldReturnByteArray() {
		Page<UserDataDTO> page = new PageImpl<>(List.of(UserMapper.toUserDataDto(userEntity)));
		String data = String.join("\n", "ID_Funcionário;Usuário;ID_Tipo;Tipo_Usuário", "1;florinda;1;GESTOR");
		UserService serviceSpy = Mockito.spy(userService);
		Mockito.doReturn(page).when(serviceSpy).getUsers("", Pageable.unpaged());
		Mockito.doReturn(data.getBytes()).when(exporterService).writeUsersAsBytes(anyList());
		byte[] csvData = serviceSpy.getUsersAsCsvData("");
		Assertions.assertEquals(data, new String(csvData));
	}
}
