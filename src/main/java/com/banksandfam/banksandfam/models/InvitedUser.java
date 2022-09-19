package com.banksandfam.banksandfam.models;

import javax.persistence.*;

@Entity
@Table(name = "invited_users")
public class InvitedUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true)
	private String invited_email;

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public InvitedUser(long id, String invited_email, User user) {
		this.id = id;
		this.invited_email = invited_email;
		this.user = user;
	}

	public InvitedUser(String invited_email) {
		this.invited_email = invited_email;
	}

	public InvitedUser() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getInvited_email() {
		return invited_email;
	}

	public void setInvited_email(String invited_email) {
		this.invited_email = invited_email;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
