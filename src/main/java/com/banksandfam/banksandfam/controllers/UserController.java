package com.banksandfam.banksandfam.controllers;

import com.banksandfam.banksandfam.models.User;
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

	@Autowired
	private UserServices service;

	public UserController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userDao = userRepository;
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
		//Setup 4 variables to help check for error and existing data
		boolean inputHasErrors = user.getUsername().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty();
		String passwordConfirmation = request.getParameter("verify-password");
		User newUser = userDao.findByUsername(user.getUsername());
		User uEmail = userDao.findByEmail(user.getEmail());
		//run the first if to verify we aren't missing any data and that an existing user and/or email was not found and that the passwords match
		if (!inputHasErrors && newUser == null && uEmail == null && (String.valueOf(user.getPassword()).equals(passwordConfirmation))) {
			//runs user creation process in the UserServices service file
			service.register(user, getSiteURL(request));
			return "reg-conf";
			//Else-if password/confirmed password do not match, redirect with a parameter
		} else if (!String.valueOf(user.getPassword()).equals(passwordConfirmation)){
			//set username & email attribute in session so that the fields repopulate on load and redirect
			rm.addFlashAttribute("uName", String.valueOf(user.getUsername()));
			rm.addFlashAttribute("email", String.valueOf(user.getEmail()));
			return "redirect:/register?pmfail";
			//Else-if an existing user was found, redirect with a parameter
		} else if (newUser != null) {
			//set email attribute in session so that the email field repopulates on load and redirect
			rm.addFlashAttribute("email", String.valueOf(user.getEmail()));
			return "redirect:/register?uexists";
			//else this handles the finding of an existing email. redirect with parameter
		} else {
			//set username attribute in session so that the username field repopulates on load and redirect
			rm.addFlashAttribute("uName", String.valueOf(user.getUsername()));
			return "redirect:/register?eexists";
		}
	}

	//method determines what the site URL should be for the creation of the link emailed to the user
	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	@GetMapping("/verify")
	public String verifyUser(@Param("code") String code) {
		//runs verification process from the UserServices service file to see if the code in the emailed link matches the one stored in the database
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
		//runs verification process from the UserServices service file to see if the code in the emailed link matches the one stored in the database
		if (service.verifyReset(code)) {
			return "reset-newpw";
		} else {
			return "reset-verify-fail";
		}
	}

	@PostMapping("/verifyreset")
	public String doReset(HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
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
