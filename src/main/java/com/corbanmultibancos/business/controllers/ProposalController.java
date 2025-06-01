package com.corbanmultibancos.business.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.services.ProposalService;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

	@Autowired
	private ProposalService proposalService;

	@GetMapping("/{id}")
	public ResponseEntity<ProposalDataDTO> getProposalById(@PathVariable Long id){
		return null;
	}

	@GetMapping
	public ResponseEntity<Page<ProposalDataDTO>> getProposals(@RequestParam(defaultValue = "") String code,
			@RequestParam(defaultValue = "") String employeeName,
			@RequestParam(defaultValue = "") Integer bankCode,
			@RequestParam(required = true) String dateField,
			@RequestParam(required = true) LocalDate beginDate,
			@RequestParam(required = true) LocalDate endDate,
			Pageable pageable){
		Page<ProposalDataDTO> page = proposalService.getProposals(null, null, null, null, null, null, pageable);
		return null;
	}

	@PostMapping
	public ResponseEntity<ProposalDataDTO> createProposal(@RequestBody ProposalCreateDTO createDto){
		return null;
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProposalDataDTO> updateProposal(@PathVariable Long id, @RequestBody ProposalCreateDTO createDto){
		return null;
	}
}