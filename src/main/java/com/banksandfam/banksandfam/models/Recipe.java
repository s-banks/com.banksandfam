package com.banksandfam.banksandfam.models;

import javax.persistence.*;

@Entity
@Table(name = "recipes")
public class Recipe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String category;

	@Column(nullable = false)
	private String title;

	@Column()
	private String recipe_front;

	@Column()
	private String recipe_back;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String recipe_full;

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public Recipe() {
	}

	public Recipe(long id, String category, String title, String recipe_front, String recipe_back, String recipe_full, User user) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.recipe_front = recipe_front;
		this.recipe_back = recipe_back;
		this.recipe_full = recipe_full;
		this.user = user;
	}

	public Recipe(String category, String title, String recipe_front, String recipe_back, String recipe_full, User user) {
		this.category = category;
		this.title = title;
		this.recipe_front = recipe_front;
		this.recipe_back = recipe_back;
		this.recipe_full = recipe_full;
		this.user = user;
	}

	public Recipe(String category, String title, String recipe_full, User user) {
		this.category = category;
		this.title = title;
		this.recipe_full = recipe_full;
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRecipe_front() {
		return recipe_front;
	}

	public void setRecipe_front(String recipe_front) {
		this.recipe_front = recipe_front;
	}

	public String getRecipe_back() {
		return recipe_back;
	}

	public void setRecipe_back(String recipe_back) {
		this.recipe_back = recipe_back;
	}

	public String getRecipe_full() {
		return recipe_full;
	}

	public void setRecipe_full(String recipe_full) {
		this.recipe_full = recipe_full;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
