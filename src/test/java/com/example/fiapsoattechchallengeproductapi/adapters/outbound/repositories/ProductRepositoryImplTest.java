package com.example.fiapsoattechchallengeproductapi.adapters.outbound.repositories;

import com.example.fiapsoattechchallengeproductapi.adapters.outbound.entities.JpaProductEntity;
import com.example.fiapsoattechchallengeproductapi.adapters.outbound.entities.JpaProductImageEntity;
import com.example.fiapsoattechchallengeproductapi.domain.Category;
import com.example.fiapsoattechchallengeproductapi.domain.Product;
import com.example.fiapsoattechchallengeproductapi.domain.ProductImage;
import com.example.fiapsoattechchallengeproductapi.exceptions.ProductNotFoundException;
import com.example.fiapsoattechchallengeproductapi.utils.mappers.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProductRepositoryImplTest {

    @Mock
    private JpaProductRepository jpaProductRepository;

    @Mock
    private JpaProductImageRepository jpaProductImageRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductRepositoryImpl productRepository;

    private Product product;
    private JpaProductEntity productEntity;
    private JpaProductImageEntity imageEntity;
    private List<Product> productList;
    private List<JpaProductEntity> productEntityList;
    private List<JpaProductImageEntity> imageEntityList;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("10.99"));
        product.setCategory(Category.LANCHE);
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(null);

        ProductImage image = new ProductImage();
        image.setUrl("http://example.com/image.jpg");
        product.setImages(Collections.singletonList(image));

        productEntity = new JpaProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Test Product");
        productEntity.setDescription("Test Description");
        productEntity.setPrice(new BigDecimal("10.99"));
        productEntity.setCategory("LANCHE");
        productEntity.setActive(true);
        productEntity.setCreatedAt(LocalDateTime.now());
        productEntity.setUpdatedAt(null);

        imageEntity = new JpaProductImageEntity();
        imageEntity.setId(1L);
        imageEntity.setProductId(1L);
        imageEntity.setUrl("http://example.com/image.jpg");
        imageEntity.setCreatedAt(LocalDateTime.now());
        imageEntity.setUpdatedAt(null);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setDescription("Test Description 2");
        product2.setPrice(new BigDecimal("20.99"));
        product2.setCategory(Category.BEBIDA);
        product2.setActive(true);
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(null);
        product2.setImages(Collections.singletonList(image));

        productList = Arrays.asList(product, product2);

        JpaProductEntity productEntity2 = new JpaProductEntity();
        productEntity2.setId(2L);
        productEntity2.setName("Test Product 2");
        productEntity2.setDescription("Test Description 2");
        productEntity2.setPrice(new BigDecimal("20.99"));
        productEntity2.setCategory("BEBIDA");
        productEntity2.setActive(true);
        productEntity2.setCreatedAt(LocalDateTime.now());
        productEntity2.setUpdatedAt(null);

        productEntityList = Arrays.asList(productEntity, productEntity2);

        JpaProductImageEntity imageEntity2 = new JpaProductImageEntity();
        imageEntity2.setId(2L);
        imageEntity2.setProductId(2L);
        imageEntity2.setUrl("http://example.com/image2.jpg");
        imageEntity2.setCreatedAt(LocalDateTime.now());
        imageEntity2.setUpdatedAt(null);

        imageEntityList = Arrays.asList(imageEntity, imageEntity2);
    }

    @Test
    void save_ShouldSaveProductAndImages() {
        when(jpaProductRepository.save(any(JpaProductEntity.class))).thenReturn(productEntity);
        when(jpaProductImageRepository.saveAll(anyList())).thenReturn(Collections.singletonList(imageEntity));
        when(mapper.jpaToDomain(any(JpaProductEntity.class), anyList())).thenReturn(product);

        Product result = productRepository.save(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(Category.LANCHE, result.getCategory());
        assertTrue(result.getActive());

        verify(jpaProductRepository, times(1)).save(any(JpaProductEntity.class));
        verify(jpaProductImageRepository, times(1)).saveAll(anyList());
        verify(mapper, times(1)).jpaToDomain(any(JpaProductEntity.class), anyList());
    }

    @Test
    void findAll_ShouldReturnProductList() {
        when(jpaProductRepository.findAllById(anyList())).thenReturn(productEntityList);
        when(jpaProductImageRepository.findAll()).thenReturn(imageEntityList);
        when(mapper.jpaToDomainList(anyList(), anyList())).thenReturn(productList);

        List<Product> result = productRepository.findAll(Arrays.asList(1L, 2L));

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(jpaProductRepository, times(1)).findAllById(anyList());
        verify(jpaProductImageRepository, times(1)).findAll();
        verify(mapper, times(1)).jpaToDomainList(anyList(), anyList());
    }

    @Test
    void findById_WhenProductExists_ShouldReturnProduct() {
        when(jpaProductRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        when(jpaProductImageRepository.findByProductId(1L)).thenReturn(Collections.singletonList(imageEntity));
        when(mapper.jpaToDomain(any(JpaProductEntity.class), anyList())).thenReturn(product);

        Optional<Product> result = productRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Product", result.get().getName());
        assertEquals(Category.LANCHE, result.get().getCategory());

        verify(jpaProductRepository, times(1)).findById(1L);
        verify(jpaProductImageRepository, times(1)).findByProductId(1L);
        verify(mapper, times(1)).jpaToDomain(any(JpaProductEntity.class), anyList());
    }

    @Test
    void findById_WhenProductDoesNotExist_ShouldThrowException() {
        when(jpaProductRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productRepository.findById(99L));

        verify(jpaProductRepository, times(1)).findById(99L);
        verify(jpaProductImageRepository, never()).findByProductId(anyLong());
        verify(mapper, never()).jpaToDomain(any(JpaProductEntity.class), anyList());
    }

    @Test
    void findProductByCategory_ShouldReturnProductList() {
        when(jpaProductRepository.findProductByCategory("LANCHE")).thenReturn(productEntityList);
        when(jpaProductImageRepository.findAll()).thenReturn(imageEntityList);
        when(mapper.jpaToDomainList(anyList(), anyList())).thenReturn(productList);

        List<Product> result = productRepository.findProductByCategory(Category.LANCHE);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(jpaProductRepository, times(1)).findProductByCategory("LANCHE");
        verify(jpaProductImageRepository, times(1)).findAll();
        verify(mapper, times(1)).jpaToDomainList(anyList(), anyList());
    }

    @Test
    void deleteById_ShouldDisableProductWhenProductExists() {
        when(jpaProductRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        when(jpaProductRepository.save(any(JpaProductEntity.class))).thenReturn(productEntity);

        productRepository.deleteById(1L);

        verify(jpaProductRepository, times(1)).findById(1L);
        verify(jpaProductRepository, times(1)).save(productEntity);
        assertFalse(productEntity.getActive());
        assertNotNull(productEntity.getUpdatedAt());
    }

    @Test
    void deleteById_ShouldThrowExceptionWhenProductDoesNotExist() {
        when(jpaProductRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productRepository.deleteById(99L));

        verify(jpaProductRepository, times(1)).findById(99L);
        verify(jpaProductRepository, never()).save(any(JpaProductEntity.class));
    }

    @Test
    void updateProduct_ShouldUpdateProductAndImages() {
        when(jpaProductRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        when(jpaProductRepository.save(any(JpaProductEntity.class))).thenReturn(productEntity);
        doNothing().when(jpaProductImageRepository).deleteAllImagesByProductId(1L);
        when(jpaProductImageRepository.saveAll(anyList())).thenReturn(Collections.singletonList(imageEntity));
        when(mapper.jpaToDomain(any(JpaProductEntity.class), anyList())).thenReturn(product);

        Product result = productRepository.updateProduct(1L, product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(Category.LANCHE, result.getCategory());

        verify(jpaProductRepository, times(1)).findById(1L);
        verify(jpaProductImageRepository, times(1)).deleteAllImagesByProductId(1L);
        verify(jpaProductRepository, times(1)).save(any(JpaProductEntity.class));
        verify(jpaProductImageRepository, times(1)).saveAll(anyList());
        verify(mapper, times(1)).jpaToDomain(any(JpaProductEntity.class), anyList());
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldThrowException() {
        when(jpaProductRepository.findById(99L)).thenThrow(new RuntimeException("Product not found"));

        assertThrows(RuntimeException.class, () -> productRepository.updateProduct(99L, product));

        verify(jpaProductRepository, times(1)).findById(99L);
        verify(jpaProductImageRepository, never()).deleteAllImagesByProductId(anyLong());
        verify(jpaProductRepository, never()).save(any(JpaProductEntity.class));
        verify(jpaProductImageRepository, never()).saveAll(anyList());
        verify(mapper, never()).jpaToDomain(any(JpaProductEntity.class), anyList());
    }
}