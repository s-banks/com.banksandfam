package com.banksandfam.banksandfam.controllers;

import com.banksandfam.banksandfam.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	private final UserRepository userDao;

	public HomeController(UserRepository userDao) {
		this.userDao = userDao;
	}

	@GetMapping("/")
	public String home(){
		return "index";
	}

	@GetMapping("/recipes")
	public String showRecipes(){
		return "recipes";
	}

	@GetMapping("/history")
	public String showHistory(){
		return "history";
	}


}
