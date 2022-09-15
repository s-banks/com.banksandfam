package com.banksandfam.banksandfam.services;

import com.banksandfam.banksandfam.models.User;
import com.banksandfam.banksandfam.models.UserWithRoles;
import com.banksandfam.banksandfam.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


@Service
public class UserDetailsLoader extends User implements UserDetailsService {
	private final UserRepository users;
	public UserDetailsLoader(UserRepository users) {
		this.users = users;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = users.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("No user found for " + username);
		}
		return new UserWithRoles(user);
	}
}
