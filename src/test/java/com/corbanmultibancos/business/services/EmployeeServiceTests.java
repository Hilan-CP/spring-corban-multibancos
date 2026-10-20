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

import com.corbanmultibancos.business.dto.EmployeeCreateDTO;
import com.corbanmultibancos.business.dto.EmployeeUserDTO;
import com.corbanmultibancos.business.entities.Employee;
import com.corbanmultibancos.business.mappers.EmployeeMapper;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

	@InjectMocks
	private EmployeeService employeeService;

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private EmployeeCsvExporterService exporterService;

	private Long existingId;
	private Long nonExistingId;
	private String existingCpf;
	private String nonExistingCpf;
	private String partialName;
	private Employee employeeEntity;
	private EmployeeCreateDTO employeeCreationDto;
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
	}

	@Test
	public void getEmployeeByIdShouldReturnEmployeeUserDTOWhenExistingId() {
		Mockito.when(employeeRepository.findById(existingId)).thenReturn(Optional.of(employeeEntity));
		EmployeeUserDTO employeeDto = employeeService.getEmployeeById(existingId);
		Assertions.assertEquals(existingId, employeeDto.getId());
	}

	@Test
	public void getEmployeeByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Mockito.when(employeeRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Assertions.assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(nonExistingId));
	}

	@Test
	public void getEmployeesShouldReturnPageOfSingleEmployeeUserDTOWhenExistingCpf() {
		Mockito.when(employeeRepository.findByCpf(existingCpf)).thenReturn(Optional.of(employeeEntity));
		Page<EmployeeUserDTO> employeeDtoPage = employeeService.getEmployees(existingCpf, "", pageable);
		Assertions.assertEquals(1, employeeDtoPage.getSize());
		Mockito.verify(employeeRepository, times(1)).findByCpf(any());
		Mockito.verify(employeeRepository, never()).findByNameContainingIgnoreCase(any(), any(Pageable.class));
		Mockito.verify(employeeRepository, never()).findAll(any(Pageable.class));
	}

	@Test
	public void getEmployeesShouldReturnEmptyPageWhenNonExistingCpf() {
		Mockito.when(employeeRepository.findByCpf(nonExistingCpf)).thenReturn(Optional.empty());
		Page<EmployeeUserDTO> employeeDtoPage = employeeService.getEmployees(nonExistingCpf, "", pageable);
		Assertions.assertTrue(employeeDtoPage.isEmpty());
		Mockito.verify(employeeRepository, times(1)).findByCpf(any());
		Mockito.verify(employeeRepository, never()).findByNameContainingIgnoreCase(any(), any(Pageable.class));
		Mockito.verify(employeeRepository, never()).findAll(any(Pageable.class));
	}

	@Test
	public void getEmployeesShouldReturnPageOfEmployeeUserDTOWhenPartialName() {
		Mockito.when(employeeRepository.findByNameContainingIgnoreCase(partialName, pageable)).thenReturn(employeePage);
		Page<EmployeeUserDTO> employeeDtoPage = employeeService.getEmployees("", partialName, pageable);
		Assertions.assertFalse(employeeDtoPage.isEmpty());
		Mockito.verify(employeeRepository, never()).findByCpf(any());
		Mockito.verify(employeeRepository, times(1)).findByNameContainingIgnoreCase(any(), any(Pageable.class));
		Mockito.verify(employeeRepository, never()).findAll(any(Pageable.class));
	}

	@Test
	public void getEmployeesShouldReturnPageOfEmployeeUserDTOWhenNoParameter() {
		Mockito.when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
		Page<EmployeeUserDTO> employeeDtoPage = employeeService.getEmployees("", "", pageable);
		Assertions.assertFalse(employeeDtoPage.isEmpty());
		Mockito.verify(employeeRepository, never()).findByCpf(any());
		Mockito.verify(employeeRepository, never()).findByNameContainingIgnoreCase(any(), any(Pageable.class));
		Mockito.verify(employeeRepository, times(1)).findAll(any(Pageable.class));
	}

	@Test
	public void getEmployeesShouldThrowIllegalParameterExceptionWhenAllParameters() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> employeeService.getEmployees(existingCpf, partialName, pageable));
	}

	@Test
	public void createEmployeeShouldReturnEmployeeCreationDTO() {
		Mockito.when(employeeRepository.save(any())).thenReturn(employeeEntity);
		EmployeeCreateDTO employeeDto = employeeService.createEmployee(employeeCreationDto);
		Assertions.assertNotNull(employeeDto.getId());
	}

	@Test
	public void updateEmployeeShouldReturnEmployeeCreationDTOWhenExistingId() {
		Mockito.when(employeeRepository.getReferenceById(existingId)).thenReturn(employeeEntity);
		Mockito.when(employeeRepository.save(any())).thenReturn(employeeEntity);
		EmployeeCreateDTO employeeDto = employeeService.updateEmployee(existingId, employeeCreationDto);
		Assertions.assertNotNull(employeeDto.getId());
	}

	@Test
	public void updateEmployeeShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Mockito.when(employeeRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> employeeService.updateEmployee(nonExistingId, employeeCreationDto));
	}

	@Test
	public void getEmployeesAsCsvDataShouldReturnByteArray() {
		Page<EmployeeUserDTO> page = new PageImpl<>(List.of(EmployeeMapper.toEmployeeUserDTO(employeeEntity)));
		String data = String.join(";", "ID;CPF;Nome;Usuário;Tipo_Usuário;ID_Equipe;Nome_Equipe",
				"1;10975759000;Florinda Flores;;");
		EmployeeService serviceSpy = Mockito.spy(employeeService);
		Mockito.doReturn(page).when(serviceSpy).getEmployees("", "", Pageable.unpaged());
		Mockito.doReturn(data.getBytes()).when(exporterService).writeEmployeeAsBytes(anyList());
		byte[] csvData = serviceSpy.getEmployeesAsCsvData("", "");
		Assertions.assertEquals(data, new String(csvData));
	}
}
