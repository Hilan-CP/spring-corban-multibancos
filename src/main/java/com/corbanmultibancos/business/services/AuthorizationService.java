package com.corbanmultibancos.business.services;

import com.corbanmultibancos.business.entities.User;
import com.corbanmultibancos.business.repositories.UserRepository;
import com.corbanmultibancos.business.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizationService {

	@Autowired
	private UserRepository userRepository;

	public void authorizeAdminOrOwner(Long employeeId) {
		User loggedUser = getLoggedUser();
		boolean admin = isAdmin(loggedUser);
		boolean owner = loggedUser.getId().equals(employeeId);
		if (!admin && !owner) {
			throw new ForbiddenException();
		}
	}

	@Transactional(readOnly = true)
	public User getLoggedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		String username = principal.getUsername();
		return userRepository.findByUsername(username).get();
	}

	public boolean isAdmin(User user) {
		return user.getRole().getAuthority().equals("GESTOR");
	}
}
