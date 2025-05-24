package com.corbanmultibancos.business.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.corbanmultibancos.business.dto.UserDTO;
import com.corbanmultibancos.business.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public UserDTO getUserById(Long id) {
		return null;
	}

	public Page<UserDTO> getUsers(String username, Pageable pageable) {
		return null;
	}

	public UserDTO createUser(UserDTO userDto) {
		return null;
	}

	public UserDTO updateUser(Long id, UserDTO userDto) {
		return null;
	}

	public void deleteUser(Long id) {

	}
}
