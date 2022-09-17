package com.banksandfam.banksandfam.services;

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
		//Get and hash the password
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		//save the new hashed password to the user object
		user.setPassword(encodedPassword);
		//create random code to function as the verification code and save to the user object
		String randomCode = RandomString.make(64);
		user.setVerificationCode(randomCode);
		//set the user enabled flag to false in order to prevent login until the user is verified and save the user to the database
		user.setEnabled(false);
		repo.save(user);
		//run the email method by passing the created user object and URL for the site
		sendVerificationEmail(user, siteURL);
	}

	private void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
		//build email message content using the passed user object and URL from the above register function
		String toAddress = user.getEmail();
		String fromAddress = "site@banksandfam.com";
		String senderName = "Banksandfam";
		String subject = "Please verify your registration";
		String content = "Dear [[name]],"
				+ "<br>"
				+ "Please click the link below to verify your registration:"
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
		//send message, credentials in application.properties
		mailSender.send(message);
	}

	public boolean verify(String verificationCode) {
		//find the user with the matching verification code token in the email link
		User user = repo.findByVerificationCode(verificationCode);
		//if a user with the code isn't found or the user is already enabled, fail the verification
		if (user == null || user.isEnabled()) {
			return false;
		} else {
			//if the user is valid and the code matches, remove the code and enable the user, and save to the database and return true so login continues
			user.setVerificationCode(null);
			user.setEnabled(true);
			repo.save(user);
			return true;
		}
	}

	public void reset(User user, String siteURL) throws UnsupportedEncodingException, MessagingException {
		String randomCode = RandomString.make(64);
		user.setVerificationCode(randomCode);
		//set the user enabled flag to false in order to prevent login until the user is verified and save the user to the database
		user.setEnabled(false);
		user.setUsername(user.getUsername());
		repo.save(user);
		//run the email method by passing the created user object and URL for the site
		sendResetEmail(user, siteURL);
	}

	private void sendResetEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
		//build email message content using the passed user object and URL from the above register function
		String toAddress = user.getEmail();
		String fromAddress = "site@banksandfam.com";
		String senderName = "Banksandfam";
		String subject = "Please verify you wish to reset your password";
		String content = "Dear [[name]],"
				+ "<br>"
				+ "Please click the link below to reset your password:"
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
		//send message, credentials in application.properties
		mailSender.send(message);
	}

	public boolean verifyReset(String verificationCode) {
		User user = repo.findByVerificationCode(verificationCode);
		if (user == null || user.isEnabled()) {
			return false;
		} else {
			return true;
		}
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
		//build email message content using the passed user object and URL from the above register function
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

}