package com.banksandfam.banksandfam.controllers;

import com.banksandfam.banksandfam.models.Recipe;
import com.banksandfam.banksandfam.models.User;
import com.banksandfam.banksandfam.repositories.RecipeRepository;
import com.banksandfam.banksandfam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class HomeController {
	private final UserRepository userDao;
	private final RecipeRepository recipeDao;

	public HomeController(UserRepository userDao, RecipeRepository recipeDao) {
		this.userDao = userDao;
		this.recipeDao = recipeDao;
	}

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/recipes")
	public String showRecipes(Model model) {
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
	public String createRecipeForm(Model model) {
		model.addAttribute("fileApi", fileApi);
		model.addAttribute("newRecipe", new Recipe());
		return "recipe-create";
	}

	@PostMapping("/recipes/create")
	public String createRecipe(@ModelAttribute Recipe recipe) {
		if (!recipe.getTitle().isEmpty() && !recipe.getRecipe_front().isEmpty() && !recipe.getRecipe_back().isEmpty() && !recipe.getRecipe_full().isEmpty()) {
			try {
				User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				recipe.setUser(currentUser);
				recipeDao.save(recipe);
				return "redirect:/recipes";
			} catch (Exception e) {
				return "redirect:/recipes/create";
			}
		} else {
			return "redirect:/recipes/create";
		}
	}

	@GetMapping("/history")
	public String showHistory() {
		return "history";
	}

}
