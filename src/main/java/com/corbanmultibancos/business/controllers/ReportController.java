package com.corbanmultibancos.business.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.corbanmultibancos.business.dto.ReportTotalDTO;
import com.corbanmultibancos.business.services.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@PreAuthorize("hasAuthority('SCOPE_GESTOR')")
	@GetMapping
	public ResponseEntity<ReportTotalDTO> getReport(@RequestParam(required = true) List<Long> teamIds) {
		ReportTotalDTO report = reportService.getReport(teamIds);
		return ResponseEntity.ok(report);
	}
}
