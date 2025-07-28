package com.example.fiapsoattechchallengeproductapi.adapters.inbound;

import com.example.fiapsoattechchallengeproductapi.application.usecase.ProductUseCase;
import com.example.fiapsoattechchallengeproductapi.domain.Category;
import com.example.fiapsoattechchallengeproductapi.domain.ProductDTO;
import com.example.fiapsoattechchallengeproductapi.domain.ProductImageDTO;
import com.example.fiapsoattechchallengeproductapi.exceptions.ProductNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProductUseCase productUseCase;

    @InjectMocks
    private ProductController productController;

    private ProductDTO productDTO;
    private List<ProductDTO> productDTOList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
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
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        when(productUseCase.createProduct(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10.99))
                .andExpect(jsonPath("$.category").value("LANCHE"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.images[0].url").value("http://example.com/image.jpg"));

        verify(productUseCase, times(1)).createProduct(any(ProductDTO.class));
    }

    @Test
    void findProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        when(productUseCase.findProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10.99))
                .andExpect(jsonPath("$.category").value("LANCHE"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.images[0].url").value("http://example.com/image.jpg"));

        verify(productUseCase, times(1)).findProductById(1L);
    }

    @Test
    void findProductById_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(productUseCase.findProductById(99L)).thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(get("/product/99"))
                .andExpect(status().isNotFound());

        verify(productUseCase, times(1)).findProductById(99L);
    }

    @Test
    void findProductByCategory_WhenProductsExist_ShouldReturnProducts() throws Exception {
        when(productUseCase.findProductByCategory(Category.LANCHE)).thenReturn(productDTOList);

        mockMvc.perform(get("/product?category=LANCHE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].category").value("LANCHE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test Product 2"))
                .andExpect(jsonPath("$[1].category").value("BEBIDA"));

        verify(productUseCase, times(1)).findProductByCategory(Category.LANCHE);
    }

    @Test
    void findProductByCategory_WhenNoProductsExist_ShouldReturnNotFound() throws Exception {
        when(productUseCase.findProductByCategory(Category.SOBREMESA)).thenThrow(new ProductNotFoundException("No products found"));

        mockMvc.perform(get("/product?category=SOBREMESA"))
                .andExpect(status().isNotFound());

        verify(productUseCase, times(1)).findProductByCategory(Category.SOBREMESA);
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setId(1L);
        updatedProductDTO.setName("Updated Product");
        updatedProductDTO.setDescription("Updated Description");
        updatedProductDTO.setPrice(new BigDecimal("15.99"));
        updatedProductDTO.setCategory(Category.LANCHE);
        updatedProductDTO.setActive(true);
        updatedProductDTO.setCreatedAt(productDTO.getCreatedAt());
        updatedProductDTO.setUpdatedAt(LocalDateTime.now());
        updatedProductDTO.setImages(productDTO.getImages());

        when(productUseCase.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(updatedProductDTO);

        mockMvc.perform(patch("/product/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProductDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(15.99))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        verify(productUseCase, times(1)).updateProduct(eq(1L), any(ProductDTO.class));
    }

    @Test
    void disableProductById_WhenProductExists_ShouldReturnOk() throws Exception {
        doNothing().when(productUseCase).disableProductById(1L);

        mockMvc.perform(delete("/product/1"))
                .andExpect(status().isOk());

        verify(productUseCase, times(1)).disableProductById(1L);
    }

    @Test
    void disableProductById_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        doThrow(new ProductNotFoundException("Product not found")).when(productUseCase).disableProductById(99L);

        mockMvc.perform(delete("/product/99"))
                .andExpect(status().isNotFound());

        verify(productUseCase, times(1)).disableProductById(99L);
    }
}