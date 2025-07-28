package com.example.fiapsoattechchallengeproductapi.integration;

import com.example.fiapsoattechchallengeproductapi.domain.Category;
import com.example.fiapsoattechchallengeproductapi.domain.ProductDTO;
import com.example.fiapsoattechchallengeproductapi.domain.ProductImageDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Integration Test Product");
        productDTO.setDescription("Integration Test Description");
        productDTO.setPrice(new BigDecimal("15.99"));
        productDTO.setCategory(Category.LANCHE);
        
        ProductImageDTO imageDTO = new ProductImageDTO();
        imageDTO.setUrl("http://example.com/integration-test.jpg");
        productDTO.setImages(Collections.singletonList(imageDTO));

        MvcResult result = mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Test Product"))
                .andExpect(jsonPath("$.price").value(15.99))
                .andExpect(jsonPath("$.category").value("LANCHE"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.images[0].url").value("http://example.com/integration-test.jpg"))
                .andReturn();

        ProductDTO createdProduct = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ProductDTO.class
        );

        assertNotNull(createdProduct.getId());
        assertEquals("Integration Test Product", createdProduct.getName());
        assertEquals(new BigDecimal("15.99"), createdProduct.getPrice());
        assertEquals(Category.LANCHE, createdProduct.getCategory());
        assertTrue(createdProduct.getActive());
        assertEquals(1, createdProduct.getImages().size());
        assertEquals("http://example.com/integration-test.jpg", createdProduct.getImages().get(0).getUrl());

        mockMvc.perform(get("/product/" + createdProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdProduct.getId()))
                .andExpect(jsonPath("$.name").value("Integration Test Product"));
    }

    @Test
    void findProductById_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/product/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findProductByCategory_ShouldReturnProductsInCategory() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Integration Test Drink");
        productDTO.setDescription("Integration Test Drink Description");
        productDTO.setPrice(new BigDecimal("5.99"));
        productDTO.setCategory(Category.BEBIDA);
        
        ProductImageDTO imageDTO = new ProductImageDTO();
        imageDTO.setUrl("http://example.com/drink.jpg");
        productDTO.setImages(Collections.singletonList(imageDTO));

        MvcResult createResult = mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andReturn();

        ProductDTO createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProductDTO.class
        );

        MvcResult findResult = mockMvc.perform(get("/product?category=BEBIDA"))
                .andExpect(status().isOk())
                .andReturn();

        List<ProductDTO> products = objectMapper.readValue(
                findResult.getResponse().getContentAsString(),
                new TypeReference<List<ProductDTO>>() {}
        );

        assertFalse(products.isEmpty());
        
        boolean foundCreatedProduct = products.stream()
                .anyMatch(p -> p.getId().equals(createdProduct.getId()));
        assertTrue(foundCreatedProduct);
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Product To Update");
        productDTO.setDescription("Original Description");
        productDTO.setPrice(new BigDecimal("10.99"));
        productDTO.setCategory(Category.ACOMPANHAMENTO);
        
        ProductImageDTO imageDTO = new ProductImageDTO();
        imageDTO.setUrl("http://example.com/original.jpg");
        productDTO.setImages(Collections.singletonList(imageDTO));

        MvcResult createResult = mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andReturn();

        ProductDTO createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProductDTO.class
        );

        createdProduct.setName("Updated Product Name");
        createdProduct.setDescription("Updated Description");
        createdProduct.setPrice(new BigDecimal("12.99"));
        
        ProductImageDTO updatedImageDTO = new ProductImageDTO();
        updatedImageDTO.setUrl("http://example.com/updated.jpg");
        createdProduct.setImages(Collections.singletonList(updatedImageDTO));

        MvcResult updateResult = mockMvc.perform(patch("/product/" + createdProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdProduct.getId()))
                .andExpect(jsonPath("$.name").value("Updated Product Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.price").value(12.99))
                .andExpect(jsonPath("$.images[0].url").value("http://example.com/updated.jpg"))
                .andReturn();

        ProductDTO updatedProduct = objectMapper.readValue(
                updateResult.getResponse().getContentAsString(),
                ProductDTO.class
        );

        assertEquals("Updated Product Name", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
        assertEquals(new BigDecimal("12.99"), updatedProduct.getPrice());
        assertEquals("http://example.com/updated.jpg", updatedProduct.getImages().get(0).getUrl());
    }

    @Test
    void disableProductById_ShouldDisableProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Product To Disable");
        productDTO.setDescription("Will be disabled");
        productDTO.setPrice(new BigDecimal("8.99"));
        productDTO.setCategory(Category.SOBREMESA);

        ProductImageDTO imageDTO = new ProductImageDTO();
        imageDTO.setUrl("http://example.com/disable.jpg");
        productDTO.setImages(Collections.singletonList(imageDTO));

        MvcResult createResult = mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String createdJson = createResult.getResponse().getContentAsString();
        ProductDTO createdProduct = objectMapper.readValue(createdJson, ProductDTO.class);
        
        assertTrue(createdProduct.getActive());

        mockMvc.perform(delete("/product/" + createdProduct.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/product/" + createdProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andReturn();
    }
}