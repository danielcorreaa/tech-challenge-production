package com.techchallenge.domain.valueobject;


import com.techchallenge.domain.entity.Production;

import java.util.List;

public class Product {

	private String sku;
	private String title;
	private String category;
	private String description;
	private String image;


	public Product(String sku, String title, String category, String description, String image) {
		this.sku =  Validation.validateSku(sku);
		this.title = Validation.validateTitle(title);
		this.category = Validation.validateCategory(category);
		this.description =  Validation.validateDescription(description);
		this.image = image;
	}

	public String getSku() {
		return sku;
	}

	public String getTitle() {
		return title;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}
}
