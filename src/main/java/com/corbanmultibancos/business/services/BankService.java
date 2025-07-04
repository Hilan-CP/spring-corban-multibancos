package com.corbanmultibancos.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.BankDTO;
import com.corbanmultibancos.business.entities.Bank;
import com.corbanmultibancos.business.mappers.BankMapper;
import com.corbanmultibancos.business.repositories.BankRepository;
import com.corbanmultibancos.business.services.exceptions.IllegalParameterException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;
import com.corbanmultibancos.business.util.ErrorMessage;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BankService {

	@Autowired
	private BankRepository bankRepository;
	
	@Autowired
	private BankCsvExporterService exporterService;

	@Transactional(readOnly = true)
	public BankDTO getBankById(Long id) {
		Optional<Bank> result = bankRepository.findById(id);
		Bank bank = result.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.BANK_NOT_FOUND));
		return BankMapper.toDto(bank);
	}

	@Transactional(readOnly = true)
	public List<BankDTO> getBanks(Integer code, String name) {
		validateParameters(code, name);
		List<Bank> bankList;
		if (code != 0) {
			bankList = getBankByCode(code);
		} else if (!name.isBlank()) {
			bankList = bankRepository.findByName(name);
		} else {
			bankList = bankRepository.findAll();
		}
		return bankList.stream().map(bank -> BankMapper.toDto(bank)).toList();
	}

	public byte[] getBanksAsCsvData(Integer code, String name) {
		List<BankDTO> bankDtoList = getBanks(code, name);
		return exporterService.writeBanksAsBytes(bankDtoList);
	}

	@Transactional
	public BankDTO createBank(BankDTO bankDto) {
		Bank bank = new Bank();
		BankMapper.copyDtoToEntity(bankDto, bank);
		bank = bankRepository.save(bank);
		return BankMapper.toDto(bank);
	}

	@Transactional
	public BankDTO updateBank(Long id, BankDTO bankDto) {
		try {
			Bank bank = bankRepository.getReferenceById(id);
			BankMapper.copyDtoToEntity(bankDto, bank);
			bank = bankRepository.save(bank);
			return BankMapper.toDto(bank);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(ErrorMessage.BANK_NOT_FOUND);
		}
	}

	private void validateParameters(Integer code, String name) {
		if (code != 0 && !name.isBlank()) {
			throw new IllegalParameterException(ErrorMessage.MULTIPLE_PARAMS);
		}
	}

	private List<Bank> getBankByCode(Integer code) {
		Optional<Bank> result = bankRepository.findByCode(code);
		if (result.isPresent()) {
			return List.of(result.get());
		}
		return List.of();
	}
}
