package aku0003.SpringProject.controllers;

import aku0003.SpringProject.models.Customer;
import aku0003.SpringProject.models.CustomerDto;
import aku0003.SpringProject.services.CustomersRepository;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger logger = LogManager.getLogger(CustomerController.class);


    @Autowired
    private CustomersRepository repo;

    @GetMapping({"", "/"})
    public String showCustomersList(Model model) {
        List<Customer> customers = repo.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("customers", customers);
        logger.info("Successfully retrieved customer list.");
        return "products/customers";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        CustomerDto customerDto = new CustomerDto();
        model.addAttribute("customerDto", customerDto);
        logger.info("Show create customer page.");
        return "products/AddCustomer";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute CustomerDto customerDto,
                                BindingResult result){
        if (result.hasErrors()){
            return "products/AddCustomer";
        }

        Customer customer = new Customer();
        customer.setName(customerDto.getName());
        customer.setAddress(customerDto.getAddress());
        LocalDateTime now = LocalDateTime.now();
        Date createdAt = java.sql.Timestamp.valueOf(now);
        customer.setCreatedAt(createdAt);

        repo.save(customer);
        logger.info("Successfully created customer: {}", customer.getName());
        return "redirect:/customers/";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id){

        try{
            Customer customer = repo.findById(id).get();
            repo.delete(customer);
            logger.info("Successfully deleted customer with ID: {}", id);
        }
        catch (Exception ex){
            logger.error("Error occurred while deleting customer with ID {}: {}", id, ex.getMessage());
        }

        return "redirect:/customers";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id){

        try {
            Customer customer = repo.findById(id).get();
            model.addAttribute("customer", customer);

            CustomerDto customerDto = new CustomerDto();
            customerDto.setName(customer.getName());
            customerDto.setAddress(customer.getAddress());

            model.addAttribute("customerDto", customerDto);
            logger.info("Show edit customer page for customer with ID: {}", id);
        }
        catch (Exception ex){
            logger.error("Error occurred while showing edit page for customer with ID {}: {}", id, ex.getMessage());
            return "redirect:/customers/";
        }

        return "products/EditCustomer";
    }

    @PostMapping("/edit")
    public String updateCustomer(Model model, @RequestParam int id,
                                 @Valid @ModelAttribute CustomerDto customerDto,
                                 BindingResult result){
        try {
            Customer customer = repo.findById(id).get();
            model.addAttribute("customer", customer);
            if (result.hasErrors()) {
                return "products/EditCustomer";
            }
            customer.setName(customerDto.getName());
            customer.setAddress(customerDto.getAddress());
            repo.save(customer);
            logger.info("Successfully updated customer with ID: {}", id);
        } catch (Exception ex){
            logger.error("Error occurred while updating customer with ID {}: {}", id, ex.getMessage());
        }
        return "redirect:/customers/";
    }
}
