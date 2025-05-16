package com.corbanmultibancos.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.corbanmultibancos.business.dto.BankDTO;
import com.corbanmultibancos.business.entities.Bank;
import com.corbanmultibancos.business.repositories.BankRepository;

@Service
public class BankService {

	@Autowired
	private BankRepository bankRepository;
	
	public BankDTO findById(Long id) {
		return null;
	}
	
	public BankDTO findByCode(Integer code) {
		return null;
	}
	
	public List<BankDTO> findByName(String name) {
		return null;
	}
	
	public List<BankDTO> findAll(){
		return null;
	}
	
	public BankDTO insert(BankDTO bankDTO) {
		return null;
	}
	
	public BankDTO update(Long id, BankDTO bankDTO) {
		return null;
	}
}
