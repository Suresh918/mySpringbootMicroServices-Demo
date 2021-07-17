package com.example.mirai.projectname.services.configuration.productcategory;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.example.mirai.projectname.services.configuration.productcategory.models.ProductCategory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-categories")
public class ProductCategoryController {
	private final ProductCategoryService productCategoryService;

	public ProductCategoryController(ProductCategoryService productCategoryService) {
		this.productCategoryService = productCategoryService;
	}

	@PostMapping()
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getProductCategoryAdminRoles())")
	public ResponseEntity<ProductCategory> createProductCategory(@Valid @RequestBody final ProductCategory productCategory) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(productCategoryService.createProductCategory(productCategory));
		}
		catch (EntityExistsException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductCategory> getProductCategory(@PathVariable String id) {
		Optional<ProductCategory> productCategory = productCategoryService.getProductCategory(id);
		if (productCategory.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(productCategory.get());
	}

	@GetMapping()
	public ResponseEntity<Iterable<ProductCategory>> getProductCategories() {
		Iterable<ProductCategory> productCategories = productCategoryService.getProductCategories();
		if (!productCategories.iterator().hasNext())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(productCategories);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getProductCategoryAdminRoles())")
	public ResponseEntity<Void> deleteProductCategory(@PathVariable String id) {
		try {
			productCategoryService.deleteProductCategory(id);
			return ResponseEntity.noContent().build();
		}
		catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole(@configurationServiceConfigurationProperties.getProductCategoryAdminRoles())")
	public ResponseEntity<ProductCategory> updateProductCategory(@PathVariable String id, @Valid @RequestBody final ProductCategory productCategory) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(productCategoryService.updateProductCategory(id, productCategory));
		}
		catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
}
