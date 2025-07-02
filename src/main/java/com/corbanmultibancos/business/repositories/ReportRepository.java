package com.corbanmultibancos.business.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.corbanmultibancos.business.dto.ReportItemDTO;
import com.corbanmultibancos.business.entities.Proposal;

public interface ReportRepository extends JpaRepository<Proposal, Long> {

	@Query(value = """
			SELECT
		    NEW com.corbanmultibancos.business.dto.ReportItemDTO(
		        team.name,
		        employee.name,
		        COUNT(CASE WHEN proposal.generation = :today THEN 1 END) AS countDay,
		        SUM(CASE WHEN proposal.generation = :today AND proposal.status = 'GERADA' THEN proposal.rawValue ELSE 0 END) AS generatedDay,
		        SUM(CASE WHEN proposal.generation = :today AND proposal.status = 'CONTRATADA' THEN proposal.rawValue ELSE 0 END) AS paidDay,
		        SUM(CASE WHEN proposal.status = 'CONTRATADA' THEN proposal.rawValue ELSE 0 END) AS paidMonth
		    )
			FROM Proposal proposal
			JOIN proposal.employee employee
			LEFT JOIN employee.team team
			WHERE proposal.generation BETWEEN :begin AND :end
			    AND team.id IN :teams
			GROUP BY employee.name, team.name
			""")
	List<ReportItemDTO> findReportByDates(LocalDate begin, LocalDate end, LocalDate today, List<Long> teams);
}
