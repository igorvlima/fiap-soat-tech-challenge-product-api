package com.example.fiapsoattechchallengeproductapi.application.service;

import com.example.fiapsoattechchallengeproductapi.application.usecase.ProductUseCase;
import com.example.fiapsoattechchallengeproductapi.domain.Category;
import com.example.fiapsoattechchallengeproductapi.domain.ProductDTO;
import com.example.fiapsoattechchallengeproductapi.domain.ProductRepository;
import com.example.fiapsoattechchallengeproductapi.utils.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductUseCase {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        var product = productRepository.save(mapper.DTOtoDomain(productDTO));
        return mapper.domainToDTO(product);
    }

    @Override
    public ProductDTO findProductById(Long id) {
        var product = productRepository.findById(id).orElse(null);
        return mapper.domainToDTO(product);
    }

    @Override
    public List<ProductDTO> findProductByCategory(Category category) {
        var products = productRepository.findProductByCategory(category);
        return mapper.domainToDTOList(products);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        var product = productRepository.updateProduct(id, mapper.DTOtoDomain(productDTO));
        return mapper.domainToDTO(product);
    }

    @Override
    @Transactional
    public void disableProductById(Long id) {
        productRepository.deleteById(id);
    }
}