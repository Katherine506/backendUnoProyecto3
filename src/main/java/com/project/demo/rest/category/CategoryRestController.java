package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/category")
public class CategoryRestController {

    @Autowired
    private CategoryRepository categoryRepository;

    //La consulta Categorías será permitida para todos los usuarios si están autenticados
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER')")
    public List<Category> getAllCategorys(){
        return categoryRepository.findAll();
    }

    //El registro de categoría únicamente podrá realizarlo el usuario con rol SUPER-ADMIN-ROLE
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(category.getName());
                    existingCategory.setDescription(category.getDescription());
                    return categoryRepository.save(existingCategory);
                })
                .orElseGet(() -> {
                    category.setId(id);
                    return categoryRepository.save(category);
                });
    }

    //La actualizacion de categoría únicamente podrá realizarlo el usuario con rol SUPER-ADMIN-ROLE
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Category addCategory(@RequestBody Category category) {
        return  categoryRepository.save(category);
    }

    //El borrado de categoría únicamente podrá realizarlo el usuario con rol SUPER-ADMIN-ROLE
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategory (@PathVariable Long id) {
        categoryRepository.deleteById(id);
    }
}
