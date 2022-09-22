package com.banksandfam.banksandfam.controllers;

import com.banksandfam.banksandfam.repositories.RecipeRepository;
import com.banksandfam.banksandfam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
	private final UserRepository userDao;
	private final RecipeRepository recipeDao;

	public HomeController(UserRepository userDao, RecipeRepository recipeDao) {
		this.userDao = userDao;
		this.recipeDao = recipeDao;
	}

	@GetMapping("/")
	public String home(){
		return "index";
	}

	@GetMapping("/recipes")
	public String showRecipes(Model model){
		model.addAttribute("recipes", recipeDao.findAll());
		return "recipes";
	}

	@RequestMapping(path = "/recipes/{id}", method = RequestMethod.GET)
	public String fullRecipe(@PathVariable Long id, Model model) {
		model.addAttribute("recipe", recipeDao.findRecipeById(id));
		return "recipe-full";
	}

	@Value("${spring.file.api}")
	private String fileApi;

	@GetMapping("/recipes/create")
	public String createRecipes(Model model){
		model.addAttribute("fileApi", fileApi);
		return "recipe-create";
	}

	@PostMapping("/recipes/create")
	public String userContact(HttpServletRequest request, Model model) {
		return "contact";
	}

	@GetMapping("/history")
	public String showHistory(){
		return "history";
	}


}
