package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;

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

import com.corbanmultibancos.business.dto.EmployeeCreationDTO;
import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.entities.Team;
import com.corbanmultibancos.business.mappers.EmployeeMapper;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class EmployeeServiceTests {

	@InjectMocks
	private EmployeeService employeeService;

	@Mock
	private EmployeeRepository employeeRepository;

	private Long existingId;
	private Long nonExistingId;
	private String existingCpf;
	private String nonExistingCpf;
	private String partialName;
	private Employee employeeEntity;
	private EmployeeCreationDTO employeeCreationDto;
	private Page<Employee> employeePage;
	private Pageable pageable;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		existingCpf = "10975759000";
		nonExistingCpf = "00011122233";
		partialName = "jo";
		employeeEntity = new Employee(existingId, existingCpf, "Florinda Flores", null, null);
		employeeCreationDto = EmployeeMapper.toEmployeeCreationDto(employeeEntity);
		employeePage = new PageImpl<>(List.of(employeeEntity));
		pageable = PageRequest.of(0, 10);
		Mockito.when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employeeEntity));
		Mockito.when(employeeRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(employeeRepository.findByCpf(existingCpf)).thenReturn(Optional.of(employeeEntity));
		Mockito.when(employeeRepository.findByCpf(nonExistingCpf)).thenReturn(Optional.empty());
		Mockito.when(employeeRepository.findByNameContainingIgnoreCase(partialName, pageable)).thenReturn(employeePage);
		Mockito.when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
		Mockito.when(employeeRepository.getReferenceById(existingId)).thenReturn(employeeEntity);
		Mockito.when(employeeRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(employeeRepository.save(any())).thenReturn(employeeEntity);
	}

	@Test
	public void getEmployeeByIdShouldReturnEmployeeUserDTOWhenExistingId() {
		EmployeeUserDTO employeeDto = employeeService.getEmployeeById(existingId);
		Assertions.assertEquals(existingId, employeeDto.getId());
	}

	@Test
	public void getEmployeeByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(nonExistingId));
	}

	@Test
	public void getEmployeesShouldReturnPageOfSingleEmployeeUserDTOWhenExistingCpf() {
		Page<EmployeeUserDTO> employeeDtoPage = employeeService.getEmployees(existingCpf, "", pageable);
		Assertions.assertEquals(1, employeeDtoPage.getSize());
	}

	@Test
	public void getEmployeesShouldReturnEmptyPageWhenNonExistingCpf() {
		Page<EmployeeUserDTO> employeeDtoPage = employeeService.getEmployees(nonExistingCpf, "", pageable);
		Assertions.assertTrue(employeeDtoPage.isEmpty());
	}

	@Test
	public void getEmployeesShouldReturnPageOfEmployeeUserDTOWhenPartialName() {
		Page<EmployeeUserDTO> employeeDtoPage = employeeService.getEmployees("", partialName, pageable);
		Assertions.assertFalse(employeeDtoPage.isEmpty());
	}

	@Test
	public void getEmployeesShouldReturnPageOfEmployeeUserDTOWhenNoParameter() {
		Page<EmployeeUserDTO> employeeDtoPage = employeeService.getEmployees("", "", pageable);
		Assertions.assertFalse(employeeDtoPage.isEmpty());
	}

	@Test
	public void getEmployeesShouldThrowIllegalParameterExceptionWhenAllParameters() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> employeeService.getEmployees(existingCpf, partialName, pageable));
	}

	@Test
	public void createEmployeeShouldReturnEmployeeCreationDTO() {
		EmployeeCreationDTO employeeDto = employeeService.createEmployee(employeeCreationDto);
		Assertions.assertNotNull(employeeDto.getId());
	}

	@Test
	public void updateEmployeeShouldReturnEmployeeCreationDTOWhenExistingId() {
		EmployeeCreationDTO employeeDto = employeeService.updateEmployee(existingId, employeeCreationDto);
		Assertions.assertNotNull(employeeDto.getId());
	}

	@Test
	public void updateEmployeeShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> employeeService.updateEmployee(nonExistingId, employeeCreationDto));
	}
}
