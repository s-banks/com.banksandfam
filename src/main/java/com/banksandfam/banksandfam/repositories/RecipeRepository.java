package com.banksandfam.banksandfam.repositories;

import com.banksandfam.banksandfam.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	Recipe findRecipeById(Long id);
}
