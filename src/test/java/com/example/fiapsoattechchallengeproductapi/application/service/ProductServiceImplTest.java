package com.example.fiapsoattechchallengeproductapi.application.service;

import com.example.fiapsoattechchallengeproductapi.domain.*;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDTO productDTO;
    private Product product;
    private List<ProductDTO> productDTOList;
    private List<Product> productList;

    @BeforeEach
    void setUp() {

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(new BigDecimal("10.99"));
        productDTO.setCategory(Category.LANCHE);
        productDTO.setActive(true);
        productDTO.setCreatedAt(LocalDateTime.now());
        productDTO.setUpdatedAt(null);

        ProductImageDTO imageDTO = new ProductImageDTO();
        imageDTO.setUrl("http://example.com/image.jpg");
        productDTO.setImages(Collections.singletonList(imageDTO));

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

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setName("Test Product 2");
        productDTO2.setDescription("Test Description 2");
        productDTO2.setPrice(new BigDecimal("20.99"));
        productDTO2.setCategory(Category.BEBIDA);
        productDTO2.setActive(true);
        productDTO2.setCreatedAt(LocalDateTime.now());
        productDTO2.setUpdatedAt(null);
        productDTO2.setImages(Collections.singletonList(imageDTO));

        productDTOList = Arrays.asList(productDTO, productDTO2);

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
    }

    @Test
    void createProduct_ShouldReturnCreatedProductDTO() {
        when(mapper.DTOtoDomain(any(ProductDTO.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(mapper.domainToDTO(any(Product.class))).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(new BigDecimal("10.99"), result.getPrice());
        assertEquals(Category.LANCHE, result.getCategory());
        assertTrue(result.getActive());
        assertEquals(1, result.getImages().size());
        assertEquals("http://example.com/image.jpg", result.getImages().get(0).getUrl());

        verify(mapper, times(1)).DTOtoDomain(any(ProductDTO.class));
        verify(productRepository, times(1)).save(any(Product.class));
        verify(mapper, times(1)).domainToDTO(any(Product.class));
    }

    @Test
    void findProductById_WhenProductExists_ShouldReturnProductDTO() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mapper.domainToDTO(any(Product.class))).thenReturn(productDTO);

        ProductDTO result = productService.findProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(Category.LANCHE, result.getCategory());

        verify(productRepository, times(1)).findById(1L);
        verify(mapper, times(1)).domainToDTO(any(Product.class));
    }

    @Test
    void findProductById_WhenProductDoesNotExist_ShouldReturnNull() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ProductDTO result = productService.findProductById(99L);

        assertNull(result);

        verify(productRepository, times(1)).findById(99L);
        verify(mapper, never()).domainToDTO(any(Product.class));
    }

    @Test
    void findProductByCategory_WhenProductsExist_ShouldReturnProductDTOList() {
        when(productRepository.findProductByCategory(Category.LANCHE)).thenReturn(productList);
        when(mapper.domainToDTOList(anyList())).thenReturn(productDTOList);

        List<ProductDTO> result = productService.findProductByCategory(Category.LANCHE);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test Product", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Test Product 2", result.get(1).getName());

        verify(productRepository, times(1)).findProductByCategory(Category.LANCHE);
        verify(mapper, times(1)).domainToDTOList(anyList());
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProductDTO() {
        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setId(1L);
        updatedProductDTO.setName("Updated Product");
        updatedProductDTO.setDescription("Updated Description");
        updatedProductDTO.setPrice(new BigDecimal("15.99"));
        updatedProductDTO.setCategory(Category.LANCHE);
        updatedProductDTO.setActive(true);
        updatedProductDTO.setImages(productDTO.getImages());

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(new BigDecimal("15.99"));
        updatedProduct.setCategory(Category.LANCHE);
        updatedProduct.setActive(true);
        updatedProduct.setImages(product.getImages());

        when(mapper.DTOtoDomain(any(ProductDTO.class))).thenReturn(updatedProduct);
        when(productRepository.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);
        when(mapper.domainToDTO(any(Product.class))).thenReturn(updatedProductDTO);

        ProductDTO result = productService.updateProduct(1L, updatedProductDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Product", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(new BigDecimal("15.99"), result.getPrice());

        verify(mapper, times(1)).DTOtoDomain(any(ProductDTO.class));
        verify(productRepository, times(1)).updateProduct(eq(1L), any(Product.class));
        verify(mapper, times(1)).domainToDTO(any(Product.class));
    }

    @Test
    void disableProductById_ShouldCallRepositoryDeleteById() {
        doNothing().when(productRepository).deleteById(1L);

        productService.disableProductById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }
}