package com.example.mirai.projectname.services.configuration.productcategory;

import com.example.mirai.projectname.services.configuration.productcategory.models.ProductCategory;

import org.springframework.data.repository.CrudRepository;

public interface ProductCategoryRepository extends CrudRepository<ProductCategory, String> {
}
