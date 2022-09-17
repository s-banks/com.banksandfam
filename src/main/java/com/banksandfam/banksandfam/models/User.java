package com.banksandfam.banksandfam.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String firstName;

	@Column
	private String lastName;

	@Column(length = 50, nullable = false, unique = true)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(length = 100)
	private String city;

	@Column(length = 50)
	private String state;

	@Column()
	private String profile_img = "https://cdn.filestackcontent.com/RUeUf2m3SiCYjVoIVXIm";

	@Column(name = "verification_code", length = 64)
	private String verificationCode;

	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<InvitedUser> invitedUser;

	public User() {
	}

	public User(long id, String firstName, String lastName, String username, String email, String password, String city, String state, String profile_img, String verificationCode, boolean enabled) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.password = password;
		this.city = city;
		this.state = state;
		this.profile_img = profile_img;
		this.verificationCode = verificationCode;
		this.enabled = enabled;
	}

	public User(User copy) {
		id = copy.id;
		firstName = copy.firstName;
		lastName = copy.lastName;
		username = copy.username;
		email = copy.email;
		password = copy.password;
		city = copy.city;
		state = copy.state;
		profile_img = copy.profile_img;
		verificationCode = copy.verificationCode;
		enabled = copy.enabled;
	}

	public User(String firstName, String lastName, String username, String email, String password, String city, String state, String profile_img, String verificationCode, boolean enabled) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.password = password;
		this.city = city;
		this.state = state;
		this.profile_img = profile_img;
		this.verificationCode = verificationCode;
		this.enabled = enabled;
	}

	public User(String username, String email, String password, String profile_img, String verificationCode, boolean enabled) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.profile_img = profile_img;
		this.verificationCode = verificationCode;
		this.enabled = enabled;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getProfile_img() {
		return profile_img;
	}

	public void setProfile_img(String profile_img) {
		this.profile_img = profile_img;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
