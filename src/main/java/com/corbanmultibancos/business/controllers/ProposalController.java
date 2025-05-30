package com.corbanmultibancos.business.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.services.ProposalService;

@RestController
@RequestMapping("/proposals")
public class ProposalController {
	
	@Autowired
	private ProposalService proposalService;

	@GetMapping
	public ResponseEntity<Page<ProposalDataDTO>> getProposals(@RequestParam(defaultValue = "") String code,
			@RequestParam(defaultValue = "") String employeeName,
			@RequestParam(defaultValue = "") Integer bankCode,
			@RequestParam(defaultValue = "generation") String dateField,
			@RequestParam(required = true) LocalDate beginDate,
			@RequestParam(required = true) LocalDate endDate,
			Pageable pageable){
		System.out.println("code "+code);
		System.out.println("employee "+employeeName);
		System.out.println("bank "+bankCode);
		System.out.println("field "+dateField);
		System.out.println("begin "+beginDate);
		System.out.println("end "+endDate);
		Page<ProposalDataDTO> page = proposalService.getProposals(null, null, null, null, null, null, pageable);
		return ResponseEntity.ok(page);
	}
}