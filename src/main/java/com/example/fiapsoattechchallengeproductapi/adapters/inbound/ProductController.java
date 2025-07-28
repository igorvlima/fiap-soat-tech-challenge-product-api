package com.example.fiapsoattechchallengeproductapi.adapters.inbound;

import com.example.fiapsoattechchallengeproductapi.application.usecase.ProductUseCase;
import com.example.fiapsoattechchallengeproductapi.domain.Category;
import com.example.fiapsoattechchallengeproductapi.domain.ProductDTO;
import com.example.fiapsoattechchallengeproductapi.exceptions.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductUseCase productUseCase;

    @PostMapping()
    public  ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            ProductDTO product = productUseCase.createProduct(productDTO);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findProductById(@PathVariable Long id) {
        try{
            ProductDTO product = productUseCase.findProductById(id);
            return ResponseEntity.ok(product);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<ProductDTO>> findProductByCategory(@RequestParam Category category) {
        try{
            List<ProductDTO> products = productUseCase.findProductByCategory(category);
            return ResponseEntity.ok(products);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return productUseCase.updateProduct(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disableProductById(@PathVariable Long id) {
        try {
            productUseCase.disableProductById(id);
            return ResponseEntity.ok().build();
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}