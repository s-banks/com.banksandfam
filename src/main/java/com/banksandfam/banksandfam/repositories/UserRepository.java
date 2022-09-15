package com.banksandfam.banksandfam.repositories;

import com.banksandfam.banksandfam.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
		User findByUsername(String username);
		User findByVerificationCode(String code);
		User findByEmail(String email);

		List<User> findUsersByCityLikeAndStateLike(String city, String state);

	}

