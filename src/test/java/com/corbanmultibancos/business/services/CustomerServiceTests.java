package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
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

import com.corbanmultibancos.business.dto.CustomerDTO;
import com.corbanmultibancos.business.entities.Customer;
import com.corbanmultibancos.business.mappers.CustomerMapper;
import com.corbanmultibancos.business.repositories.CustomerRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class CustomerServiceTests {

	@InjectMocks
	private CustomerService customerService;

	@Mock
	private CustomerRepository customerRepository;

	private Long existingId;
	private Long nonExistingId;
	private String existingCpf;
	private String nonExistingCpf;
	private String partialName;
	private String partialPhone;
	private Customer customerEntity;
	private Page<Customer> customerPage;
	private Pageable pageable;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 1000L;
		existingCpf = "00066098645";
		nonExistingCpf = "00011122233";
		partialName = "se";
		partialPhone = "119";
		customerEntity = new Customer(existingId, existingCpf, "Sergio", "11930587328", LocalDate.parse("1969-08-13"));
		customerPage = new PageImpl<>(List.of(customerEntity));
		pageable = PageRequest.of(0, 10);
		Mockito.when(customerRepository.findById(existingId)).thenReturn(Optional.of(customerEntity));
		Mockito.when(customerRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(customerRepository.findByCpf(existingCpf)).thenReturn(Optional.of(customerEntity));
		Mockito.when(customerRepository.findByCpf(nonExistingCpf)).thenReturn(Optional.empty());
		Mockito.when(customerRepository.findByNameContainingIgnoreCase(partialName, any(Pageable.class))).thenReturn(customerPage);
		Mockito.when(customerRepository.findByPhoneContaining(partialPhone, any(Pageable.class))).thenReturn(customerPage);
		Mockito.when(customerRepository.getReferenceById(existingId)).thenReturn(customerEntity);
		Mockito.when(customerRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(customerRepository.save(any())).thenReturn(customerEntity);
	}

	@Test
	public void getCustomerByIdShouldReturnCustomerDTOWhenExistingId() {
		CustomerDTO customerDto = customerService.getCustomerById(existingId);
		Assertions.assertEquals(existingId, customerDto.getId());
	}

	@Test
	public void getCustomerByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(nonExistingId));
	}

	@Test
	public void getCustomersShouldReturnPageOfSingleCustomerDTOWhenExistingCpf() {
		Page<CustomerDTO> page = customerService.getCustomers(existingCpf, "", "", pageable);
		Assertions.assertEquals(1, page.getSize());
	}

	@Test
	public void getCustomersShouldReturnEmptyPageWhenNonExistingCpf() {
		Page<CustomerDTO> page = customerService.getCustomers(nonExistingCpf, "", "", pageable);
		Assertions.assertTrue(page.isEmpty());
	}

	@Test
	public void getCustomersShouldReturnCustomerDTOPageWhenPartialName() {
		Page<CustomerDTO> page = customerService.getCustomers("", partialName, "", pageable);
		Assertions.assertFalse(page.isEmpty());
	}

	@Test
	public void getCustomersShouldReturnCustomerDTOPageWhenPartialPhone() {
		Page<CustomerDTO> page = customerService.getCustomers("", "", partialPhone, pageable);
		Assertions.assertFalse(page.isEmpty());
	}

	@Test
	public void getCustomersShouldReturnCustomerDTOPageWhenNoParameter() {
		Page<CustomerDTO> page = customerService.getCustomers("", "", "", pageable);
		Assertions.assertFalse(page.isEmpty());
	}

	@Test
	public void getCustomersShouldThrowIllegalParameterExceptionWhenAllParameters() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> customerService.getCustomers(existingCpf, partialName, partialPhone, pageable));
	}

	@Test
	public void getCustomersShouldThrowIllegalParameterExceptionWhenExistingCpfAndPhone() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> customerService.getCustomers(existingCpf, "", partialPhone, pageable));
	}

	@Test
	public void getCustomersShouldThrowIllegalParameterExceptionWhenExistingCpfAndName() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> customerService.getCustomers(existingCpf, partialName, "", pageable));
	}

	@Test
	public void getCustomersShouldThrowIllegalParameterExceptionWhenExistingNameAndPhone() {
		Assertions.assertThrows(IllegalParameterException.class,
				() -> customerService.getCustomers("", partialName, partialPhone, pageable));
	}

	@Test
	public void createCustomerShouldReturnCustomerDTO() {
		customerEntity.setId(null);
		CustomerDTO customerDto = customerService.createCustomer(CustomerMapper.toDto(customerEntity));
		Assertions.assertNotNull(customerDto.getId());
	}

	@Test
	public void updateCustomerShouldReturnCustomerDTOWhenExistingId() {
		customerEntity.setId(null);
		CustomerDTO customerDto = customerService.updateCustomer(existingId, CustomerMapper.toDto(customerEntity));
		Assertions.assertEquals(existingId, customerDto.getId());
	}

	@Test
	public void updateCustomerShouldReturnEntityNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> customerService.updateCustomer(nonExistingId, CustomerMapper.toDto(customerEntity)));
	}
}
