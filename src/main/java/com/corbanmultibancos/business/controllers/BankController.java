package com.corbanmultibancos.business.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.corbanmultibancos.business.dto.BankDTO;
import com.corbanmultibancos.business.services.BankService;

@RestController
@RequestMapping("/banks")
public class BankController {

	@Autowired
	private BankService bankService;

	@GetMapping("/{id}")
	public ResponseEntity<BankDTO> getBankById(@PathVariable Long id) {
		BankDTO bankDto = bankService.getBankById(id);
		return ResponseEntity.ok(bankDto);
	}

	@GetMapping
	public ResponseEntity<List<BankDTO>> getBanks(@RequestParam(defaultValue = "0") Integer code,
												@RequestParam(defaultValue = "") String name) {
		List<BankDTO> bankDtoList = bankService.getBanks(code, name);
		return ResponseEntity.ok(bankDtoList);
	}

	@PostMapping
	public ResponseEntity<BankDTO> createBank(@RequestBody BankDTO bankDto) {
		bankDto = bankService.createBank(bankDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(bankDto.getId());
		return ResponseEntity.created(uri).body(bankDto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BankDTO> updateBank(@PathVariable Long id, @RequestBody BankDTO bankDto) {
		bankDto = bankService.updateBank(id, bankDto);
		return ResponseEntity.ok(bankDto);
	}
}
