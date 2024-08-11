package aku0003.SpringProject.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private int id;
    //@NotBlank(message = "Order date must be provided")
    private Date orderDate;
    @NotNull(message = "The status is required")
    private String status;
    //@NotBlank(message = "Customer id is required")
    private int customerId;
    private String customerName;
    @NotNull(message = "At least one product is required")
    private List<Product> products;
}
