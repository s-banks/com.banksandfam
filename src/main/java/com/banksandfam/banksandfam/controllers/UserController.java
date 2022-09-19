package com.banksandfam.banksandfam.controllers;

import com.banksandfam.banksandfam.models.InvitedUser;
import com.banksandfam.banksandfam.models.User;
import com.banksandfam.banksandfam.repositories.InvitedUserRepository;
import com.banksandfam.banksandfam.repositories.UserRepository;
import com.banksandfam.banksandfam.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class UserController {
	public static void main(String[] args) {

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hash = passwordEncoder.encode("password");
		System.out.println(hash);
	}

	private PasswordEncoder passwordEncoder;
	private UserRepository userDao;
	private InvitedUserRepository invitedUserDao;

	@Autowired
	private UserServices service;

	public UserController(PasswordEncoder passwordEncoder, UserRepository userRepository, InvitedUserRepository invitedUserRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userDao = userRepository;
		this.invitedUserDao = invitedUserRepository;
	}

	@GetMapping("/register")
	public String registrationForm(Model model) {
		try {
			User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			model.addAttribute("currentUser", currentUser);
			return "redirect:/profile";
		} catch (Exception e) {
			model.addAttribute("newUser", new User());
			return "register";
		}
	}

	@PostMapping("/register")
	public String processRegister(User user, HttpServletRequest request, RedirectAttributes rm) throws UnsupportedEncodingException, MessagingException {
		boolean inputHasErrors = user.getUsername().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty();
		String passwordConfirmation = request.getParameter("verify-password");
		User newUser = userDao.findByUsername(user.getUsername());
		User uEmail = userDao.findByEmail(user.getEmail());
		System.out.println(user.getEmail());
		InvitedUser invitedUser = invitedUserDao.findInvitedUser(user.getEmail());
		if (!inputHasErrors && newUser == null && uEmail == null && (String.valueOf(user.getPassword()).equals(passwordConfirmation)) && invitedUser != null) {
			service.register(user, getSiteURL(request));
			invitedUserDao.deleteInvitedUser(invitedUser.getInvited_email());
			return "reg-conf";
		} else if (!String.valueOf(user.getPassword()).equals(passwordConfirmation)){
			rm.addFlashAttribute("uName", String.valueOf(user.getUsername()));
			rm.addFlashAttribute("email", String.valueOf(user.getEmail()));
			return "redirect:/register?pmfail";
		} else if (newUser != null) {
			rm.addFlashAttribute("email", String.valueOf(user.getEmail()));
			return "redirect:/register?uexists";
		} else if (invitedUser == null) {
			return "redirect:/register?notinvited";
		} else {
			rm.addFlashAttribute("uName", String.valueOf(user.getUsername()));
			return "redirect:/register?eexists";
		}
	}

	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	@GetMapping("/verify")
	public String verifyUser(@Param("code") String code) {
		if (service.verify(code)) {
			return "verify-success";
		} else {
			return "verify-fail";
		}
	}

	@GetMapping("/resetpw")
	public String resetPassword() {
		return "reset-pw-email";
	}

	@PostMapping("/resetpw")
	public String processReset(User user, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		boolean inputHasErrors = user.getEmail().isEmpty();
		User resetUser = userDao.findByEmail(user.getEmail());
		if (!inputHasErrors && resetUser != null) {
			service.reset(resetUser, getSiteURL(request));
			return "pw-reset-conf";
		} else {
			return "redirect:/resetpw?efail";
		}
	}

	@GetMapping("/verifyreset")
	public String verifyReset(@Param("code") String code) {
		if (service.verifyReset(code)) {
			return "reset-newpw";
		} else {
			return "reset-verify-fail";
		}
	}

	@PostMapping("/verifyreset")
	public String doReset(HttpServletRequest request) {
		String password = request.getParameter("password");
		String passwordConfirmation = request.getParameter("verify-password");
		String code = request.getParameter("code");
		User editUser = userDao.findByVerificationCode(code);
		boolean inputHasErrors = password.isEmpty() || passwordConfirmation.isEmpty();
		if (!inputHasErrors && (password.equals(passwordConfirmation))) {
			String encodedPassword = passwordEncoder.encode(password);
			editUser.setPassword(encodedPassword);
			service.newPw(editUser);
			return "reset-complete";
		} else {
			return "redirect:verifyreset?pfail&code=" + code;
		}
	}

	@PostMapping("/contact")
	public String userContact(User user, HttpServletRequest request, Model model) throws MessagingException, UnsupportedEncodingException {
		model.addAttribute("subject", request.getParameter("subject"));
		model.addAttribute("body", request.getParameter("msgbody"));
		User toUser = userDao.findByUsername(request.getParameter("toUser"));
		service.send(toUser, getSiteURL(request), model);
		return "contact-success";
	}


}
