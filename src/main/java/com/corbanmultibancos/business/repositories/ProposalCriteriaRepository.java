package com.corbanmultibancos.business.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.corbanmultibancos.business.dto.ProposalDataDTO;
import com.corbanmultibancos.business.dto.ProposalFilterDTO;

public interface ProposalCriteriaRepository {

	Page<ProposalDataDTO> findByFilter(ProposalFilterDTO filter, Pageable pageable);
}
