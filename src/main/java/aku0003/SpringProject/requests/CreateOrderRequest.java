package aku0003.SpringProject.requests;

import aku0003.SpringProject.models.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private int customerId;
    private String orderDate;
    private String status;
    private List<Product> products;
}