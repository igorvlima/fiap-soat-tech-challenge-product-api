package com.example.fiapsoattechchallengeproductapi.application.usecase;

import com.example.fiapsoattechchallengeproductapi.domain.Category;
import com.example.fiapsoattechchallengeproductapi.domain.ProductDTO;

import java.util.List;

public interface ProductUseCase {

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO findProductById(Long id);

    List<ProductDTO> findProductByCategory(Category category);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void disableProductById(Long id);
}
