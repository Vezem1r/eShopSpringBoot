package aku0003.SpringProject.controllers;

import aku0003.SpringProject.models.*;
import aku0003.SpringProject.requests.CreateOrderRequest;
import aku0003.SpringProject.services.CustomersRepository;
import aku0003.SpringProject.services.OrdersRepository;
import aku0003.SpringProject.services.ProductsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@Log4j2
public class OrderController {

    @Autowired
    private OrdersRepository orderService;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @GetMapping({"", "/"})
    public String showOrdersPage(Model model) {
        try {
            List<Order> orders = orderService.findAll(Sort.by(Sort.Direction.DESC,"id"));
            model.addAttribute("orders", orders);
            log.info("Successfully retrieved orders list.");
        } catch (Exception ex) {
            log.error("Error occurred while retrieving orders list: {}", ex.getMessage());
        }
        return "products/orders";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        try {
            List<Product> products = productsRepository.findAll();
            model.addAttribute("products", products);
            List<Customer> customers = customersRepository.findAll();
            model.addAttribute("customers", customers);
            model.addAttribute("order", new Order());
            log.info("Show create order page.");
        } catch (Exception ex) {
            log.error("Error occurred while showing create order page: {}", ex.getMessage());
        }
        return "products/CreateOrder";
    }

    @PostMapping("/create")
    public String createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        try {
            Optional<Customer> optionalCustomer = customersRepository.findById(createOrderRequest.getCustomerId());
            if (optionalCustomer.isEmpty()) {
                return "redirect:/orders/create?error=CustomerNotFound";
            }

            Order order = new Order();
            order.setCustomer(optionalCustomer.get());
            LocalDate currentDate = LocalDate.now();
            order.setOrderDate(java.sql.Date.valueOf(currentDate));
            order.setStatus(createOrderRequest.getStatus());

            List<Product> products = createOrderRequest.getProducts();
            for (Product product : products) {
                Optional<Product> optionalProduct = productsRepository.findById(product.getId());
                if (optionalProduct.isPresent()) {
                    order.addProduct(optionalProduct.get());
                } else {
                    return "redirect:/orders/create?error=ProductNotFound";
                }
            }

            orderService.save(order);
            log.info("Successfully created order with ID: {}", order.getId());
            return "redirect:/orders/";
        } catch (Exception ex) {
            log.error("Error occurred while creating order: {}", ex.getMessage());
            return "redirect:/orders/";
        }
    }

    @GetMapping("/delete")
    public String deleteOrder(Model model, @RequestParam int id) {
        try {
            Optional<Order> optionalOrder = orderService.findById(id);
            if (optionalOrder.isEmpty()) {
                return "redirect:/orders?error=OrderNotFound";
            }

            Order order = optionalOrder.get();

            order.removeOrder();

            orderService.delete(order);
            log.info("Successfully deleted order with ID: {}", id);
        } catch (Exception ex) {
            log.error("Error occurred while deleting order with ID {}: {}", id, ex.getMessage());
        }

        return "redirect:/orders";
    }



    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id){

        try {
            Order order = orderService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
            OrderDto orderDto = new OrderDto();

            List<Product> products = productsRepository.findAll();

            model.addAttribute("order", order);
            model.addAttribute("orderDto", orderDto);
            model.addAttribute("products", products);
            log.info("Show edit order page for order with ID: {}", id);
        } catch (Exception ex){
            log.error("Error occurred while showing edit order page: {}", ex.getMessage());
            return "redirect:/orders/";
        }
        return "products/EditOrder";
    }

    @PostMapping("/edit")
    public String editOrder(@ModelAttribute("orderDto") OrderDto orderDto) {
        try {
            Order existingOrder = orderService.findById(orderDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + orderDto.getId()));

            existingOrder.setStatus(orderDto.getStatus());

            orderService.save(existingOrder);
            log.info("Successfully edited order with ID: {}", orderDto.getId());
            return "redirect:/orders/";
        } catch (Exception ex) {
            log.error("Error occurred while editing order: {}", ex.getMessage());
            return "redirect:/orders/";
        }
    }
}