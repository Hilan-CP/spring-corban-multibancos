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
				report.team,
			    report.employee,
			    COUNT(CASE WHEN report.generation = :today THEN 1 END) AS count_day,
			    SUM(CASE WHEN report.generation = :today AND report.status = 'GERADA' THEN report.raw_value ELSE 0 END) AS generated_day,
			    SUM(CASE WHEN report.generation = :today AND report.status = 'CONTRATADA' THEN report.raw_value ELSE 0 END) AS paid_day,
			    SUM(CASE WHEN report.status = 'CONTRATADA' THEN report.raw_value ELSE 0 END) AS paid_month
			FROM (
			    SELECT 
			        proposal.id,
			        proposal.raw_value,
			        proposal.status,
			        proposal.generation,
			        proposal.payment,
			        employee.name AS employee,
			        team.name AS team
			    FROM tb_proposal AS proposal
			    INNER JOIN tb_employee AS employee ON proposal.employee_id = employee.id
			    LEFT JOIN tb_team AS team ON employee.team_id = team.id
			    WHERE proposal.generation BETWEEN :begin AND :end
			    	AND team.id IN :teams
			) AS report
			GROUP BY report.employee, report.team
			""",
			nativeQuery = true)
	List<ReportItemDTO> findReportByDates(LocalDate begin, LocalDate end, LocalDate today, List<Long> teams);
}
