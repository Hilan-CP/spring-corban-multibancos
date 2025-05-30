package com.corbanmultibancos.business.mappers;

import java.time.LocalDate;
import java.util.List;

import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.entities.Proposal;
import com.corbanmultibancos.business.entities.ProposalStatus;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

public class ProposalTupleMapper {

	public static List<Selection<?>> getSelections(Root<Proposal> proposal) {
		List<Selection<?>> selections = List.of(
				proposal.get("id").alias("id"),
				proposal.get("code").alias("code"),
				proposal.get("rawValue").alias("rawValue"),
				proposal.get("generation").alias("generation"),
				proposal.get("payment").alias("payment"),
				proposal.get("status").alias("status"),
				proposal.get("employee").get("name").alias("employeeName"),
				proposal.get("bank").get("name").alias("bankName"),
				proposal.get("customer").get("cpf").alias("customerCpf"),
				proposal.get("customer").get("name").alias("customerName"));
		return selections;
	}

	public static ProposalDataDTO tupleToProposalDataDto(Tuple tuple) {
		return new ProposalDataDTO(
				tuple.get("id", Long.class),
				tuple.get("code", String.class),
				tuple.get("rawValue", Double.class),
				tuple.get("generation", LocalDate.class),
				tuple.get("payment", LocalDate.class),
				tuple.get("status", ProposalStatus.class),
				tuple.get("employeeName", String.class),
				tuple.get("bankName", String.class),
				tuple.get("customerCpf", String.class),
				tuple.get("customerName", String.class));
	}
}
