package com.corbanmultibancos.business.repositories;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.corbanmultibancos.business.dto.ProposalDataDTO;

public interface ProposalCriteriaRepository {

	Page<ProposalDataDTO> findBy(Map<String, Object> filter, Pageable pageable);
}
