package aku0003.SpringProject.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto {

    @NotBlank(message = "The name should not be empty")
    private String name;
    @NotBlank(message = "The address should not be empty")
    private String address;
}
