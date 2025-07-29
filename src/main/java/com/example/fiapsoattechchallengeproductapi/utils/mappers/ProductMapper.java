package com.example.fiapsoattechchallengeproductapi.utils.mappers;

import com.example.fiapsoattechchallengeproductapi.adapters.outbound.entities.JpaProductEntity;
import com.example.fiapsoattechchallengeproductapi.adapters.outbound.entities.JpaProductImageEntity;
import com.example.fiapsoattechchallengeproductapi.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public Product jpaToDomain(JpaProductEntity jpaProductEntity, List<JpaProductImageEntity> jpaProductImageEntity) {
        if (jpaProductEntity == null) {
            return null;
        }

        Product product = new Product();
        product.setId(jpaProductEntity.getId());
        product.setName(jpaProductEntity.getName());
        product.setDescription(jpaProductEntity.getDescription());
        product.setPrice(jpaProductEntity.getPrice());
        product.setCategory(Category.valueOf(jpaProductEntity.getCategory()));
        product.setActive(jpaProductEntity.getActive());
        product.setCreatedAt(jpaProductEntity.getCreatedAt());
        product.setUpdatedAt(jpaProductEntity.getUpdatedAt());

        List<JpaProductImageEntity> filteredImages = jpaProductImageEntity.stream()
                .filter(image -> image.getProductId().equals(jpaProductEntity.getId()))
                .toList();

        product.setImages(mapImageJpaToDomainList(filteredImages));
        return product;
    }

    public Product DTOtoDomain(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setActive(productDTO.getActive());
        product.setCreatedAt(productDTO.getCreatedAt());
        product.setUpdatedAt(productDTO.getUpdatedAt());
        product.setImages(productDTO.getImages().stream()
                .map(this::mapImageDTOToDomain)
                .toList()
        );
        return product;
    }

    public ProductDTO domainToDTO(Product product) {

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setCategory(product.getCategory());
        productDTO.setActive(product.getActive());
        productDTO.setCreatedAt(product.getCreatedAt());
        productDTO.setUpdatedAt(product.getUpdatedAt());
        if (product.getImages() != null) {
            productDTO.setImages(
                    product.getImages().stream()
                            .map(this::mapImageDomainToDTO)
                            .toList()
            );
        }
        return productDTO;
    }

    private ProductImage mapImageDTOToDomain(ProductImageDTO imageDTO) {
        ProductImage image = new ProductImage();
        image.setUrl(imageDTO.getUrl());
        return image;
    }

    private ProductImageDTO mapImageDomainToDTO(ProductImage image) {
        ProductImageDTO productImageDTO = new ProductImageDTO();
        productImageDTO.setUrl(image.getUrl());
        return productImageDTO;
    }

    private List<ProductImage> mapImageJpaToDomainList(List<JpaProductImageEntity> imageEntities) {
        if (imageEntities == null) {
            return null;
        }
        return imageEntities.stream()
                .map(imageEntity -> {
                    ProductImage image = new ProductImage();
                    image.setUrl(imageEntity.getUrl());
                    return image;
                })
                .toList();
    }

    private ProductImage mapImageJpaToDomain(JpaProductImageEntity imageEntity) {
        if (imageEntity == null) {
            return null;
        }

        ProductImage image = new ProductImage();
        image.setUrl(imageEntity.getUrl());
        return image;
    }

    public List<Product> jpaToDomainList(List<JpaProductEntity> products, List<JpaProductImageEntity> images) {
        return products.stream().map(product -> jpaToDomain(product, images)).toList();
    }

    public List<ProductDTO> domainToDTOList(List<Product> products) {
        return products.stream().map(this::domainToDTO).toList();
    }
}