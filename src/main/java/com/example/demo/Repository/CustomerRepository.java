package com.example.demo.Repository;
import com.example.demo.bean.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByPublished(boolean published);
    List<Customer> findByTitleContaining(String title);
}