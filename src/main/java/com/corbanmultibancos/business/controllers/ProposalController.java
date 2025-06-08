package com.corbanmultibancos.business.controllers;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.corbanmultibancos.business.dto.ProposalCreateDTO;
import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.services.ProposalService;
import com.corbanmultibancos.business.validations.CreateGroup;

import jakarta.validation.groups.Default;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

	@Autowired
	private ProposalService proposalService;

	@GetMapping("/{id}")
	public ResponseEntity<ProposalDataDTO> getProposalById(@PathVariable Long id){
		ProposalDataDTO proposalDataDto = proposalService.getProposalById(id);
		return ResponseEntity.ok(proposalDataDto);
	}

	@GetMapping
	public ResponseEntity<Page<ProposalDataDTO>> getProposals(@RequestParam(defaultValue = "") String code,
			@RequestParam(defaultValue = "") String employeeName,
			@RequestParam(defaultValue = "") Integer bankCode,
			@RequestParam(required = true) String dateField,
			@RequestParam(required = true) LocalDate beginDate,
			@RequestParam(required = true) LocalDate endDate,
			Pageable pageable){
		Page<ProposalDataDTO> page = proposalService.getProposals(code, employeeName, bankCode, dateField, beginDate, endDate, pageable);
		return ResponseEntity.ok(page);
	}

	@GetMapping("/csv")
	public ResponseEntity<Resource> getProposalsAsCsv(@RequestParam(defaultValue = "") String code,
			@RequestParam(defaultValue = "") String employeeName,
			@RequestParam(defaultValue = "") Integer bankCode,
			@RequestParam(required = true) String dateField,
			@RequestParam(required = true) LocalDate beginDate,
			@RequestParam(required = true) LocalDate endDate){
		byte[] csvData = proposalService.getProposalsAsCsvData(code, employeeName, bankCode, dateField, beginDate, endDate);
		Resource resource = new ByteArrayResource(csvData);
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=proposals.csv")
				.contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
				.body(resource);
	}

	@PostMapping
	public ResponseEntity<ProposalDataDTO> createProposal(@Validated(value = {Default.class, CreateGroup.class}) @RequestBody ProposalCreateDTO createDto){
		ProposalDataDTO proposalDataDto = proposalService.createProposal(createDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(proposalDataDto.getId());
		return ResponseEntity.created(uri).body(proposalDataDto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProposalDataDTO> updateProposal(@PathVariable Long id, @Validated(Default.class) @RequestBody ProposalCreateDTO createDto){
		ProposalDataDTO proposalDataDto = proposalService.updateProposal(id, createDto);
		return ResponseEntity.ok(proposalDataDto);
	}
}