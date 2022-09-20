package com.banksandfam.banksandfam.controllers;

import com.banksandfam.banksandfam.models.InvitedUser;
import com.banksandfam.banksandfam.models.User;
import com.banksandfam.banksandfam.repositories.InvitedUserRepository;
import com.banksandfam.banksandfam.repositories.UserRepository;
import com.banksandfam.banksandfam.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


@Controller
public class ProfileController {
	private final UserRepository userDao;
	private InvitedUserRepository invitedUserDao;

	public ProfileController(UserRepository userDao, InvitedUserRepository invitedUserRepository) {
		this.userDao = userDao;
		this.invitedUserDao = invitedUserRepository;
	}

	@Autowired
	private UserServices service;

	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	@GetMapping("/profile")
	public String viewProfile(Model model, User user) {
		try {
			User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userState = currentUser.getState();
			model.addAttribute("userState", userState);
			return "profile";
		} catch (Exception e) {
			return "redirect:/login";
		}
	}

	@RequestMapping(path = "/profile/{username}", method = RequestMethod.GET)
	public String postPage(@PathVariable String username, Model model) {
		model.addAttribute("user", userDao.findByUsername(username));
		return "usrprofiles";
	}

	@PostMapping("/profile/contact")
	public String userContact(HttpServletRequest request, Model model) {
		User toUser = userDao.getById(Long.valueOf(request.getParameter("toUser")));
		model.addAttribute("toUser", toUser);
		model.addAttribute("subject", request.getParameter("subject"));
		return "contact";
	}

	@PostMapping("/profile/editBio")
	public String editBio(User user) {
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		user.setUsername(currentUser.getUsername());
		user.setEmail(currentUser.getEmail());
		user.setEnabled(currentUser.isEnabled());
		user.setPassword(currentUser.getPassword());
		user.setId(currentUser.getId());
		user.setProfile_img(currentUser.getProfile_img());
		userDao.save(user);
		final Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();
		final Authentication newAuth = new PreAuthenticatedAuthenticationToken(user, oldAuth.getCredentials(), oldAuth.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		return "redirect:/profile";
	}

	@PostMapping("/profile/editDescription")
	public String editDescription(User user) {
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		user.setUsername(currentUser.getUsername());
		user.setEmail(currentUser.getEmail());
		user.setState(currentUser.getState());
		user.setCity(currentUser.getCity());
		user.setEnabled(currentUser.isEnabled());
		user.setPassword(currentUser.getPassword());
		user.setCity(currentUser.getCity());
		user.setState(currentUser.getState());
		user.setId(currentUser.getId());
		user.setProfile_img(currentUser.getProfile_img());
		userDao.save(user);
		final Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();
		final Authentication newAuth = new PreAuthenticatedAuthenticationToken(user, oldAuth.getCredentials(), oldAuth.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		return "redirect:/profile";
	}

	@PostMapping("/profile/editimg")
	public String editimg(User user) {
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		user.setUsername(currentUser.getUsername());
		user.setEmail(currentUser.getEmail());
		user.setState(currentUser.getState());
		user.setCity(currentUser.getCity());
		user.setEnabled(currentUser.isEnabled());
		user.setPassword(currentUser.getPassword());
		user.setId(currentUser.getId());
		userDao.save(user);
		final Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();
		final Authentication newAuth = new PreAuthenticatedAuthenticationToken(user, oldAuth.getCredentials(), oldAuth.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		return "redirect:/profile";
	}

	@PostMapping("/profile/inviteUser")
	public String inviteUser(HttpServletRequest request, Model model) {
		if (request.getParameter("invitedUser") != null && !request.getParameter("invitedUser").isEmpty()) {
			try {
				User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				InvitedUser invitedUser = new InvitedUser();
				invitedUser.setInvited_email(request.getParameter("invitedUser"));
				invitedUser.setUser(currentUser);
				invitedUserDao.save(invitedUser);
				if (currentUser.getFirstName() == null || currentUser.getFirstName().isEmpty()) {
					model.addAttribute("sender-name", currentUser.getUsername());
				} else {
					model.addAttribute("sender-name", currentUser.getFirstName());
				}
				service.invite(invitedUser, getSiteURL(request), model);
				return "redirect:/profile?usuccess";
			} catch (Exception e) {
				return "redirect:/profile?uexists";
			}
		} else {
			return "redirect:/profile?eempty";
		}
	}
}
