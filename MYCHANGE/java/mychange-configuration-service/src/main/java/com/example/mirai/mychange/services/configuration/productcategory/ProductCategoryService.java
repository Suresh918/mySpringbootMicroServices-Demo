package com.example.mirai.projectname.services.configuration.productcategory;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.example.mirai.projectname.services.configuration.productcategory.models.ProductCategory;
import com.example.mirai.projectname.services.configuration.util.Util;

import org.springframework.stereotype.Service;

@Service
public class ProductCategoryService {
	private final ProductCategoryRepository productCategoryRepository;

	public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
		this.productCategoryRepository = productCategoryRepository;
	}

	public ProductCategory createProductCategory(ProductCategory productCategory) {
		String name = Util.generateIdFromString(productCategory.getLabel());
		Optional<ProductCategory> existingProductCategory = getProductCategory(name);
		if (existingProductCategory.isEmpty()) {
			productCategory.setName(name);
			return productCategoryRepository.save(productCategory);
		}
		else
			throw new EntityExistsException();
	}

	public Optional<ProductCategory> getProductCategory(String productCategoryId) {
		return productCategoryRepository.findById(productCategoryId);
	}

	public Iterable<ProductCategory> getProductCategories() {
		return productCategoryRepository.findAll();
	}

	public void deleteProductCategory(String productCategoryId) {
		productCategoryRepository.deleteById(productCategoryId);
	}

	public ProductCategory updateProductCategory(String productCategoryId, ProductCategory productCategory) {
		Optional<ProductCategory> existingProductCategory = getProductCategory(productCategoryId);
		if (existingProductCategory.isEmpty())
			throw new EntityNotFoundException();
		else {
			productCategory.setName(productCategoryId);
			return productCategoryRepository.save(productCategory);
		}
	}
}
