package com.printshop.service;

import com.printshop.model.UsedMaterial;
import com.printshop.repository.UsedMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UsedMaterialService {
    private final UsedMaterialRepository usedMaterialRepository;

    public UsedMaterialService(UsedMaterialRepository usedMaterialRepository) {
        this.usedMaterialRepository = usedMaterialRepository;
    }

    public List<UsedMaterial> findAll() {
        return usedMaterialRepository.findAll();
    }
}
