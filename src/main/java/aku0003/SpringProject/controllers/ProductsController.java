package aku0003.SpringProject.controllers;

import aku0003.SpringProject.models.Product;
import aku0003.SpringProject.models.ProductDto;
import aku0003.SpringProject.services.ProductsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
@Log4j2
public class ProductsController {

    @Autowired
    private ProductsRepository repo;

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        try {
            List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC,"id"));
            model.addAttribute("products", products);
            log.info("Successfully retrieved product list.");
        } catch (Exception ex) {
            log.error("Error occurred while retrieving product list: {}", ex.getMessage());
        }
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        try {
            ProductDto productDto = new ProductDto();
            model.addAttribute("productDto", productDto);
            log.info("Show create product page.");
        } catch (Exception ex) {
            log.error("Error occurred while showing create product page: {}", ex.getMessage());
        }
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        try {
            if (productDto.getImageFile().isEmpty()) {
                result.addError(new FieldError("productDto", "imageFile", "The image file should not be empty"));
            }

            if (result.hasErrors()) {
                return "products/CreateProduct";
            }

            MultipartFile image = productDto.getImageFile();
            Date createdAt = new Date();

            String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            Product product = new Product();
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setCreatedAt(createdAt);
            product.setImageFileName(storageFileName);

            repo.save(product);

            log.info("Successfully created product: {}", product.getName());
        } catch (Exception ex) {
            log.error("Error occurred while creating product: {}", ex.getMessage());
        }
        return "redirect:/products/";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            ProductDto productDto = new ProductDto();

            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("product", product);
            model.addAttribute("productDto", productDto);

            log.info("Show edit product page for product with ID: {}", id);
        } catch (Exception ex) {
            log.error("Error occurred while showing edit product page: {}", ex.getMessage());
            return "redirect:/products/";
        }
        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam int id,
                                @Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        try {
            Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "products/EditProduct";
            }

            if (!productDto.getImageFile().isEmpty()) {
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                try {
                    Files.delete(oldImagePath);
                } catch (IOException ex) {
                    log.error("Failed to delete old image: {}", ex.getMessage());
                }

                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    log.error("Failed to save new image: {}", ex.getMessage());
                }

                product.setImageFileName(storageFileName);
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            repo.save(product);
            log.info("Successfully updated product with ID: {}", id);
        } catch (Exception ex) {
            log.error("Error occurred while updating product: {}", ex.getMessage());
        }
        return "redirect:/products/";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        try {
            Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            Path imagePath = Paths.get("public/images/" + product.getImageFileName());
            Files.delete(imagePath);

            repo.delete(product);
            log.info("Successfully deleted product with ID: {}", id);
        } catch (Exception ex) {
            log.error("Error occurred while deleting product: {}", ex.getMessage());
        }
        return "redirect:/products";
    }
}
