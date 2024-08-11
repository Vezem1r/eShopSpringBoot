package aku0003.SpringProject.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
public class ProductDto {

    @NotBlank(message = "The name should not be empty")
    private String name;
    @NotBlank(message = "The brand should not be empty")
    private String brand;
    @NotBlank(message = "The category should not be empty")
    private String category;
    @Min(0)
    private double price;

    @Size(min = 10, message = "The description should be at least 10 characters")
    @Size(max = 2000, message = "The description cannot exceed 2000 characters")
    private String description;
    private MultipartFile imageFile;
}
