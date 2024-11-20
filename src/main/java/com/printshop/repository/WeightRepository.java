package com.printshop.repository;

import com.printshop.model.Weight;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {
    Optional<Weight> findByName(String name);
}