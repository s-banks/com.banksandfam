package com.banksandfam.banksandfam.services;

import com.banksandfam.banksandfam.models.InvitedUser;
import com.banksandfam.banksandfam.models.User;
import com.banksandfam.banksandfam.repositories.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class UserServices {
	@Autowired
	private UserRepository repo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

	public void register(User user, String siteURL) throws UnsupportedEncodingException, MessagingException {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		String randomCode = RandomString.make(64);
		user.setVerificationCode(randomCode);
		user.setEnabled(false);
		repo.save(user);
		sendVerificationEmail(user, siteURL);
	}

	private void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
		String toAddress = user.getEmail();
		String fromAddress = "site@banksandfam.com";
		String senderName = "Banksandfam";
		String subject = "Please verify your registration";
		String content = "Dear [[name]],"
				+ "<br>"
				+ "Please click the link below to verify your registration:"
				+ "<br>"
				+ "<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">Click to VERIFY your account</a></h3>"
				+ "<br>"
				+ "You can also copy/paste this link directly into your browser:"
				+ "<br>"
				+ "[[URL]]"
				+ "<br>"
				+ "Thank you,"
				+ "<br>"
				+ "Banksandfam";
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);
		content = content.replace("[[name]]", user.getUsername());
		String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
		content = content.replace("[[URL]]", verifyURL);
		helper.setText(content, true);
		mailSender.send(message);
	}

	public boolean verify(String verificationCode) {
		User user = repo.findByVerificationCode(verificationCode);
		if (user == null || user.isEnabled()) {
			return false;
		} else {
			user.setVerificationCode(null);
			user.setEnabled(true);
			repo.save(user);
			return true;
		}
	}

	public void reset(User user, String siteURL) throws UnsupportedEncodingException, MessagingException {
		String randomCode = RandomString.make(64);
		user.setVerificationCode(randomCode);
		user.setEnabled(false);
		user.setUsername(user.getUsername());
		repo.save(user);
		sendResetEmail(user, siteURL);
	}

	private void sendResetEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
		String toAddress = user.getEmail();
		String fromAddress = "site@banksandfam.com";
		String senderName = "Banksandfam";
		String subject = "Please verify you wish to reset your password";
		String content = "Dear [[name]],"
				+ "<br>"
				+ "Please click the link below to reset your password:"
				+ "<br>"
				+ "<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">Click to RESET your password</a></h3>"
				+ "<br>"
				+ "You can also copy/paste this link directly into your browser:"
				+ "<br>"
				+ "[[URL]]"
				+ "<br>"
				+ "Thank you,"
				+ "<br>"
				+ "Banksandfam";
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);
		content = content.replace("[[name]]", user.getUsername());
		String verifyURL = siteURL + "/verifyreset?code=" + user.getVerificationCode();
		content = content.replace("[[URL]]", verifyURL);
		helper.setText(content, true);
		mailSender.send(message);
	}

	public boolean verifyReset(String verificationCode) {
		User user = repo.findByVerificationCode(verificationCode);
		return user != null && !user.isEnabled();
	}

	public boolean newPw(User user) {
		if (user == null || user.isEnabled()) {
			return false;
		} else {
			user.setEnabled(true);
			user.setVerificationCode(null);
			repo.save(user);
			return true;
		}
	}

	public void send(User user, String siteURL, Model model) throws UnsupportedEncodingException, MessagingException {
		model.getAttribute("subject");
		model.getAttribute("body");
		sendContactEmail(user, model, siteURL);
	}

	private void sendContactEmail(User user, Model model, String siteURL) throws MessagingException, UnsupportedEncodingException {
		String toAddress = user.getEmail();
		String fromAddress = "site@banksandfam.com";
		String senderName = "Banksandfam";
		String subject = "Another user would like to contact you.";
		String content = "Dear [[name]],"
				+ "<br>"
				+ "<br>"
				+ "The user: [[from_name]] would like to contact you. Below is their message"
				+ "<br>"
				+ "<br>"
				+ "<h3>[[subject]]</h3>"
				+ "<br>"
				+ "<em>[[body]]</em>"
				+ "<br>"
				+ "<br>"
				+ "If you wish to respond, please email them directly at: [[email]]"
				+ "<br>"
				+ "<br>"
				+ "Thank you,"
				+ "<br>"
				+ "Banksandfam";
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);
		content = content.replace("[[name]]", user.getUsername());
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		content = content.replace("[[from_name]]", currentUser.getUsername());
		content = content.replace("[[subject]]", String.valueOf(model.getAttribute("subject")));
		content = content.replace("[[body]]", String.valueOf(model.getAttribute("body")));
		content = content.replace("[[email]]", currentUser.getEmail());
		helper.setText(content, true);
		mailSender.send(message);
	}

	public void invite(InvitedUser invitedUser, String siteURL, Model model) throws UnsupportedEncodingException, MessagingException {
		sendInviteEmail(invitedUser, model, siteURL);
	}

	private void sendInviteEmail(InvitedUser invitedUser, Model model, String siteURL) throws MessagingException, UnsupportedEncodingException {
		String toAddress = invitedUser.getInvited_email();
		String fromAddress = "site@banksandfam.com";
		String senderName = "Banksandfam";
		String subject = "Banksandfam Invitation";
		String content = "Greetings,"
				+ "<br>"
				+ "<br>"
				+ "The user, [[from_name]], would like to invite you to the Banksandfam site!"
				+ "<br>"
				+ "<br>"
				+ "If you wish to register for the site, please click the link below."
				+ "<br>"
				+ "<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_blank\">Click to Register with the site!</a></h3>"
				+ "<br>"
				+ "<br>"
				+ "Thank you,"
				+ "<br>"
				+ "Banksandfam";
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		content = content.replace("[[from_name]]", String.valueOf(model.getAttribute("sender-name")));
		String verifyURL = siteURL + "/register";
		content = content.replace("[[URL]]", verifyURL);
		helper.setText(content, true);
		mailSender.send(message);
	}

}