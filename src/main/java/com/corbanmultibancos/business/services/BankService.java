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
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BankService {
	private static final String BANK_NOT_FOUND = "Banco não encontrado";

	@Autowired
	private BankRepository bankRepository;

	@Transactional(readOnly = true)
	public BankDTO getBankById(Long id) {
		Optional<Bank> result = bankRepository.findById(id);
		Bank bank = result.orElseThrow(() -> new ResourceNotFoundException(BANK_NOT_FOUND));
		return BankMapper.toDto(bank);
	}

	@Transactional(readOnly = true)
	public BankDTO getBankByCode(Integer code) {
		Optional<Bank> result = bankRepository.findByCode(code);
		Bank bank = result.orElseThrow(() -> new ResourceNotFoundException(BANK_NOT_FOUND));
		return BankMapper.toDto(bank);
	}

	@Transactional(readOnly = true)
	public List<BankDTO> getBankByName(String name) {
		List<Bank> result = bankRepository.findByName(name);
		List<BankDTO> bankDtoList = result.stream().map(bank -> BankMapper.toDto(bank)).toList();
		return bankDtoList;
	}

	@Transactional(readOnly = true)
	public List<BankDTO> getAllBanks() {
		List<Bank> result = bankRepository.findAll();
		List<BankDTO> bankDtoList = result.stream().map(bank -> BankMapper.toDto(bank)).toList();
		return bankDtoList;
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
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(BANK_NOT_FOUND);
		}
	}
}
