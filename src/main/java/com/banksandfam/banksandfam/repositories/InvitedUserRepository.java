package com.banksandfam.banksandfam.repositories;

import com.banksandfam.banksandfam.models.InvitedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface InvitedUserRepository extends JpaRepository <InvitedUser,Long> {

	@Query("select distinct u from InvitedUser u where u.invited_email = ?1")
	InvitedUser findInvitedUser(String invited_email);

	@Transactional
	@Modifying
	@Query("delete from InvitedUser where invited_email = ?1")
	void deleteInvitedUser(String invited_email);

}
