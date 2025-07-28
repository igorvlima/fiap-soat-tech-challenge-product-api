package com.example.fiapsoattechchallengeproductapi.domain;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    List<Product> findAll(List<Long> ids);

    Optional<Product> findById(Long id);

    List<Product> findProductByCategory(Category category);

    Product updateProduct(Long id, Product product);

    void deleteById(Long id);
}
