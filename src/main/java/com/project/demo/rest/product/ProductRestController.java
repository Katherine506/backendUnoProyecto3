package com.project.demo.rest.product;

import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductRestController {
    @Autowired
    private ProductRepository productRepository;

    // La consulta de productos será permitida para todos los usuarios autenticados, con soporte para paginación.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productPage.getTotalPages());
        meta.setTotalElements(productPage.getTotalElements());
        meta.setPageNumber(productPage.getNumber() + 1);
        meta.setPageSize(productPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Products retrieved successfully",
                productPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    // La actualización del producto podrá ser realizada por ADMIN y SUPER_ADMIN.
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product,
            HttpServletRequest request) {

        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isPresent()) {
            Product existingProduct = foundProduct.get();
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setStockQuantity(product.getStockQuantity());
            existingProduct.setCategory(product.getCategory());

            productRepository.save(existingProduct);
            return new GlobalResponseHandler().handleResponse(
                    "Product updated successfully",
                    existingProduct,
                    HttpStatus.OK,
                    request
            );
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Product with id " + id + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }
    }

    // El registro de producto podrá ser realizado solo por ADMIN y SUPER_ADMIN.
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> addProduct(
            @RequestBody Product product,
            HttpServletRequest request) {

        Product savedProduct = productRepository.save(product);
        return new GlobalResponseHandler().handleResponse(
                "Product created successfully",
                savedProduct,
                HttpStatus.CREATED,
                request
        );
    }

    // El borrado del producto estará restringido a ADMIN y SUPER_ADMIN.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id,
            HttpServletRequest request) {

        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isPresent()) {
            productRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse(
                    "Product deleted successfully",
                    foundProduct.get(),
                    HttpStatus.OK,
                    request
            );
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Product with id " + id + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }
    }
}
