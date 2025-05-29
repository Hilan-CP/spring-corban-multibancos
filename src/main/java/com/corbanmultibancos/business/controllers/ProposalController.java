package com.corbanmultibancos.business.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.services.ProposalService;

@RestController
@RequestMapping("/proposals")
public class ProposalController {
	
	@Autowired
	private ProposalService proposalService;

	@GetMapping
	public ResponseEntity<Page<ProposalDataDTO>> getProposals(Pageable pageable){
		Page<ProposalDataDTO> page = proposalService.getProposals(null, null, null, null, null, null, pageable);
		return ResponseEntity.ok(page);
	}
}
