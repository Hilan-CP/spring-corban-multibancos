package com.corbanmultibancos.business.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.corbanmultibancos.business.dto.TeamDTO;
import com.corbanmultibancos.business.entities.Team;
import com.corbanmultibancos.business.mappers.TeamMapper;
import com.corbanmultibancos.business.repositories.EmployeeRepository;
import com.corbanmultibancos.business.repositories.TeamRepository;
import com.corbanmultibancos.business.services.exceptions.DataIntegrityException;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class TeamServiceTests {

	@InjectMocks
	private TeamService teamService;

	@Mock
	private TeamRepository teamRepository;

	@Mock
	private EmployeeRepository employeeRepository;

	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private String partialName;
	private String nonExistingName;
	private Team teamEntity;
	private List<Team> teamList;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		dependentId = 2L;
		partialName = "nior";
		nonExistingName = "equipe inexistente";
		teamEntity = new Team(1L, "Junior");
		teamList = List.of(teamEntity);
		Mockito.when(teamRepository.findById(existingId)).thenReturn(Optional.of(teamEntity));
		Mockito.when(teamRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(teamRepository.findByNameContainingIgnoreCase(partialName)).thenReturn(teamList);
		Mockito.when(teamRepository.findByNameContainingIgnoreCase(nonExistingName)).thenReturn(List.of());
		Mockito.when(teamRepository.findAll()).thenReturn(teamList);
		Mockito.when(teamRepository.getReferenceById(existingId)).thenReturn(teamEntity);
		Mockito.when(teamRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(teamRepository.save(any())).thenReturn(teamEntity);
		Mockito.when(teamRepository.existsById(existingId)).thenReturn(true);
		Mockito.when(teamRepository.existsById(nonExistingId)).thenReturn(false);
		Mockito.when(teamRepository.existsById(dependentId)).thenReturn(true);
		Mockito.doNothing().when(teamRepository).deleteById(existingId);
		Mockito.when(employeeRepository.existsByTeamId(existingId)).thenReturn(false);
		Mockito.when(employeeRepository.existsByTeamId(dependentId)).thenReturn(true);
	}

	@Test
	public void getTeamByIdShouldReturnTeamDTOWhenExistingId() {
		TeamDTO teamDto = teamService.getTeamById(existingId);
		Assertions.assertEquals(existingId, teamDto.getId());
	}

	@Test
	public void getTeamByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> teamService.getTeamById(nonExistingId));
	}

	@Test
	public void getTeamsShouldReturnTeamListWhenPartialName() {
		List<TeamDTO> teamDtoList = teamService.getTeams(partialName);
		Assertions.assertFalse(teamDtoList.isEmpty());
		Mockito.verify(teamRepository, times(1)).findByNameContainingIgnoreCase(any());
		Mockito.verify(teamRepository, never()).findAll();
	}

	@Test
	public void getTeamsShouldReturnEmptyListWhenNonExistingName() {
		List<TeamDTO> teamDtoList = teamService.getTeams(nonExistingName);
		Assertions.assertTrue(teamDtoList.isEmpty());
		Mockito.verify(teamRepository, times(1)).findByNameContainingIgnoreCase(any());
		Mockito.verify(teamRepository, never()).findAll();
	}

	@Test
	public void getTeamsShouldReturnTeamListWhenNoParameter() {
		List<TeamDTO> teamDtoList = teamService.getTeams("");
		Assertions.assertFalse(teamDtoList.isEmpty());
		Mockito.verify(teamRepository, never()).findByNameContainingIgnoreCase(any());
		Mockito.verify(teamRepository, times(1)).findAll();
	}

	@Test
	public void createTeamShouldReturnTeamDTO() {
		TeamDTO teamDto = teamService.createTeam(TeamMapper.toDto(teamEntity));
		Assertions.assertNotNull(teamDto.getId());
	}

	@Test
	public void updateTeamShouldReturnTeamDTOWhenExistingId() {
		TeamDTO teamDto = teamService.updateTeam(existingId, TeamMapper.toDto(teamEntity));
		Assertions.assertNotNull(teamDto.getId());
	}

	@Test
	public void updateTeamShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> teamService.updateTeam(nonExistingId, TeamMapper.toDto(teamEntity)));
	}

	@Test
	public void deleteTeamShouldDoNothingWhenExistingId() {
		Assertions.assertDoesNotThrow(() -> teamService.deleteTeam(existingId));
		Mockito.verify(teamRepository, times(1)).deleteById(any());
	}

	@Test
	public void deleteTeamShouldThrowResourceNotFoundExceptionWhenNonExistingId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> teamService.deleteTeam(nonExistingId));
		Mockito.verify(teamRepository, never()).deleteById(any());
	}

	@Test
	public void deleteTeamShouldThrowDataIntegrityExceptionWhenDependentId() {
		Assertions.assertThrows(DataIntegrityException.class, () -> teamService.deleteTeam(dependentId));
		Mockito.verify(teamRepository, never()).deleteById(any());
	}
}
