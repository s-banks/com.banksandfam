package com.banksandfam.banksandfam.models;

import javax.persistence.*;

public class InvitedUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true)
	private String invited_email;

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

}
