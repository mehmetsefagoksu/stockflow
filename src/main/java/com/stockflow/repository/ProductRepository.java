
package com.stockflow.repository;

import com.stockflow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    boolean existsByCategory_Id(Integer categoryId);

    List<Product> findAllByCategory_Id(Integer categoryId);

}
