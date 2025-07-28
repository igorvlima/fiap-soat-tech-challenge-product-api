package com.example.fiapsoattechchallengeproductapi.adapters.outbound.repositories;

import com.example.fiapsoattechchallengeproductapi.adapters.outbound.entities.JpaProductEntity;
import com.example.fiapsoattechchallengeproductapi.adapters.outbound.entities.JpaProductImageEntity;
import com.example.fiapsoattechchallengeproductapi.domain.Category;
import com.example.fiapsoattechchallengeproductapi.domain.Product;
import com.example.fiapsoattechchallengeproductapi.domain.ProductRepository;
import com.example.fiapsoattechchallengeproductapi.exceptions.ProductNotFoundException;
import com.example.fiapsoattechchallengeproductapi.utils.mappers.ProductMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final JpaProductImageRepository jpaProductImageRepository;
    private final ProductMapper mapper;

    public ProductRepositoryImpl(JpaProductRepository jpaProductRepository, JpaProductImageRepository jpaProductImageRepository, ProductMapper mapper) {
        this.jpaProductRepository = jpaProductRepository;
        this.jpaProductImageRepository = jpaProductImageRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        var productEntity = jpaProductRepository.save(new JpaProductEntity(product));

        var productImageEntities = product.getImages().stream()
                .map(image -> new JpaProductImageEntity(productEntity.getId(), image.getUrl(), LocalDateTime.now(), null))
                .toList();

        var images = jpaProductImageRepository.saveAll(productImageEntities);

        return mapper.jpaToDomain(productEntity, images);
    }

    @Override
    public List<Product> findAll(List<Long> ids) {
        var productEntities = jpaProductRepository.findAllById(ids);
        var productImages = jpaProductImageRepository.findAll();
        return mapper.jpaToDomainList(productEntities, productImages);
    }


    @Override
    public Optional<Product> findById(Long id) {
        JpaProductEntity productEntity = this.jpaProductRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado"));
        List<JpaProductImageEntity> imageEntity = this.jpaProductImageRepository.findByProductId(id);
        return Optional.of(mapper.jpaToDomain(productEntity, imageEntity));
    }


    @Override
    public List<Product> findProductByCategory(Category category) {
        List<JpaProductEntity> products = this.jpaProductRepository.findProductByCategory(category.toString());
        List<JpaProductImageEntity> images = this.jpaProductImageRepository.findAll();
        return mapper.jpaToDomainList(products, images);
    }

    @Override
    public void deleteById(Long id) {
        try {
            JpaProductEntity productEntity = this.jpaProductRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado"));
            
            productEntity.setActive(false);
            productEntity.setUpdatedAt(LocalDateTime.now());
            
            this.jpaProductRepository.save(productEntity);
        } catch (Exception e) {
            throw new ProductNotFoundException("Error disabling product with ID " + id + ": " + e.getMessage());
        }
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        var currentProduct = jpaProductRepository.findById(id).orElseThrow();

        currentProduct.setId(currentProduct.getId());
        currentProduct.setActive(currentProduct.getActive());
        currentProduct.setCategory(product.getCategory().toString());
        currentProduct.setDescription(product.getDescription());
        currentProduct.setName(product.getName());
        currentProduct.setPrice(product.getPrice());
        currentProduct.setCreatedAt(currentProduct.getCreatedAt());
        currentProduct.setUpdatedAt(LocalDateTime.now());

        jpaProductImageRepository.deleteAllImagesByProductId(id);

        List<JpaProductImageEntity> newImages = new ArrayList<>();
        if (product.getImages() != null) {
            newImages = product.getImages().stream()
                    .map(image -> new JpaProductImageEntity(currentProduct.getId(), image.getUrl(), LocalDateTime.now(), LocalDateTime.now()))
                    .toList();
        }
        var updatedEntity = jpaProductRepository.save(currentProduct);
        var images = jpaProductImageRepository.saveAll(newImages);

        return mapper.jpaToDomain(updatedEntity, images);
    }
}
