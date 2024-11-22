
package com.printshop.service;

import com.printshop.model.Material;
import com.printshop.model.Quality;
import com.printshop.model.Weight;
import com.printshop.repository.MaterialRepository;
import com.printshop.repository.QualityRepository;
import com.printshop.repository.WeightRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final QualityRepository qualityRepository;
    private final WeightRepository weightRepository;

    @Autowired
    public MaterialService(MaterialRepository materialRepository,
                         QualityRepository qualityRepository,
                         WeightRepository weightRepository) {
        this.materialRepository = materialRepository;
        this.qualityRepository = qualityRepository;
        this.weightRepository = weightRepository;
    }

   /* public Material createMaterial(Material material) {
        if (materialRepository.existsByCode(material.getCode())) {
            throw new RuntimeException("Material with this code already exists");
        }
        material.setStatus(Material.MaterialStatus.AVAILABLE);
        return materialRepository.save(material);
    }*/
    
    public Material createMaterial(Material material) {
        if (materialRepository.existsByCode(material.getCode())) {
            throw new RuntimeException("Ya existe material con este código");
        }
        material.setStatus(Material.MaterialStatus.DISPONIBLE);
        return materialRepository.save(material);
    }

    public Material updateMaterial(Material material) {
        if (!materialRepository.existsById(material.getId())) {
            throw new RuntimeException("Material no encontrado");
        }
        if (material.getStatus() == null) {
            material.setStatus(Material.MaterialStatus.DISPONIBLE);
        }
        return materialRepository.save(material);
    }


    public Optional<Material> findByCode(String code) {
        return materialRepository.findByCode(code);
    }

    public Optional<Material> findById(Long id) {
        return materialRepository.findById(id);
    }

    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    /*public Material updateMaterial(Material material) {
        if (!materialRepository.existsById(material.getId())) {
            throw new RuntimeException("Material not found");
        }
        return materialRepository.save(material);
    }*/

    public void deleteMaterial(Long id) {
        materialRepository.deleteById(id);
    }

    public List<Quality> getAllQualities() {
        return qualityRepository.findAll();
    }

    public List<Weight> getAllWeights() {
        return weightRepository.findAll();
    }
}