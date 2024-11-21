package com.printshop.repository;

import com.printshop.model.UsedMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsedMaterialRepository extends JpaRepository<UsedMaterial, Long> {
    boolean existsByUsedCode(String usedCode);
}
