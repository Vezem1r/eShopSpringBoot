package aku0003.SpringProject.services;

import aku0003.SpringProject.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {
}
