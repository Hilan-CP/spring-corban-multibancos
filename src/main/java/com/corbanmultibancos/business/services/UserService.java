package com.corbanmultibancos.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corbanmultibancos.business.dto.UserDTO;
import com.corbanmultibancos.business.entities.User;
import com.corbanmultibancos.business.mappers.UserMapper;
import com.corbanmultibancos.business.repositories.UserRepository;
import com.corbanmultibancos.business.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	private static final String USER_NOT_FOUND = "Usuário não encontrado";

	@Autowired
	private UserRepository userRepository;

	@Transactional(readOnly = true)
	public UserDTO getUserById(Long id) {
		Optional<User> result = userRepository.findById(id);
		User user = result.orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
		return UserMapper.toDto(user);
	}

	@Transactional(readOnly = true)
	public Page<UserDTO> getUsers(String username, Pageable pageable) {
		Page<User> userPage;
		if (!username.isBlank()) {
			userPage = getUserByUsername(username);
		}
		else {
			userPage = userRepository.findAll(pageable);
		}
		return userPage.map(user -> UserMapper.toDto(user));
	}

	@Transactional
	public UserDTO createUser(UserDTO userDto) {
		User user = new User();
		UserMapper.copyDtoToEntity(userDto, user);
		user = userRepository.save(user);
		return UserMapper.toDto(user);
	}

	@Transactional
	public UserDTO updateUser(Long id, UserDTO userDto) {
		try {
			User user = userRepository.getReferenceById(id);
			UserMapper.copyDtoToEntity(userDto, user);
			user = userRepository.save(user);
			return UserMapper.toDto(user);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(USER_NOT_FOUND);
		}
	}

	@Transactional
	public void deleteUser(Long id) {
		if(!userRepository.existsById(id)) {
			throw new ResourceNotFoundException(USER_NOT_FOUND);
		}
		userRepository.deleteById(id);
	}

	private Page<User> getUserByUsername(String username) {
		Optional<User> result = userRepository.findByUsername(username);
		if (result.isPresent()) {
			return new PageImpl<>(List.of(result.get()));
		}
		return Page.empty();
	}
}
