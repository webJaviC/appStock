package com.printshop.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.printshop.model.Material;
import com.printshop.model.MaterialAssignment;
import com.printshop.model.UsedMaterial;
import com.printshop.model.WorkOrder;
import com.printshop.repository.MaterialAssignmentRepository;
import com.printshop.repository.MaterialRepository;
import com.printshop.repository.UsedMaterialRepository;

import jakarta.transaction.Transactional;


@Service
public class InventoryUpdateService {
    private final Set<SseEmitter> emitters = Collections.synchronizedSet(new HashSet<>());
    private final MaterialRepository materialRepository;
    private final MaterialAssignmentRepository materialAssignmentRepository;
    private final UsedMaterialRepository usedMaterialRepository;

    public InventoryUpdateService(MaterialRepository materialRepository,
                                MaterialAssignmentRepository materialAssignmentRepository,
                                UsedMaterialRepository usedMaterialRepository) {
        this.materialRepository = materialRepository;
        this.materialAssignmentRepository = materialAssignmentRepository;
        this.usedMaterialRepository = usedMaterialRepository;
    }

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    public void sendUpdate(Material material) {
        Map<String, Object> update = new HashMap<>();
        update.put("code", material.getCode());
        update.put("status", material.getStatus());

        if (material.getAssignments() != null && !material.getAssignments().isEmpty()) {
            MaterialAssignment assignment = material.getAssignments().iterator().next();
            Map<String, Object> workOrderInfo = new HashMap<>();
            workOrderInfo.put("routeNumber", assignment.getWorkOrder().getRouteNumber());
            update.put("workOrder", workOrderInfo);
            update.put("orderNumber", assignment.getOrderNumber());
            update.put("updatedNetWeight", assignment.getUpdatedNetWeight());
        }

        synchronized(emitters) {
            emitters.removeIf(emitter -> {
                try {
                    emitter.send(update);
                    return false;
                } catch (IOException e) {
                    return true;
                }
            });
        }
    }

    @Transactional
    public void updateMaterialStatus(Material material, Material.MaterialStatus status) {
        material.setStatus(status);
        materialRepository.save(material);
        sendUpdate(material);
    }

    @Transactional
    public void reserveMaterial(Material material) {
        switch (material.getStatus()) {
            case RESERVED:
                throw new IllegalStateException("Material is already reserved for another work order");
            case USED:
                throw new IllegalStateException("Material has already been used");
            case AVAILABLE:
                updateMaterialStatus(material, Material.MaterialStatus.RESERVED);
                break;
            default:
                throw new IllegalStateException("Invalid material status");
        }
    }

    @Transactional
    public void releaseMaterial(Material material) {
        if (material.getStatus() != Material.MaterialStatus.RESERVED) {
            throw new IllegalStateException("Material is not currently reserved");
        }
        updateMaterialStatus(material, Material.MaterialStatus.AVAILABLE);
    }

    @Transactional
    public void processUsedMaterial(MaterialAssignment assignment, WorkOrder workOrder) {
        Material material = assignment.getMaterial();
        double originalNetWeight = material.getNetWeight();
        double usedWeight = assignment.getUpdatedNetWeight();
        double remainingWeight = originalNetWeight - usedWeight;

        // Create used material record
        UsedMaterial usedMaterial = new UsedMaterial();
        usedMaterial.setOriginalCode(material.getCode());
        usedMaterial.setUsedCode(generateUsedMaterialCode(material.getCode()));
        usedMaterial.setQuality(material.getQuality());
        usedMaterial.setWeight(material.getWeight());
        usedMaterial.setGrossWeight(calculateProportionalWeight(material.getGrossWeight(), 
                                                              originalNetWeight, usedWeight));
        usedMaterial.setNetWeight(usedWeight);
        usedMaterial.setWidth(material.getWidth());
        usedMaterial.setLength(material.getLength());
        usedMaterial.setReceipt(material.getReceipt());
        usedMaterial.setWorkOrder(workOrder);
        usedMaterialRepository.save(usedMaterial);

        // Update original material with remaining weight
        if (remainingWeight > 0) {
            material.setNetWeight(remainingWeight);
            material.setGrossWeight(calculateProportionalWeight(material.getGrossWeight(), 
                                                             originalNetWeight, remainingWeight));
            material.setStatus(Material.MaterialStatus.AVAILABLE);
            materialRepository.save(material);
        } else {
            // If no weight remains, mark as used
            material.setStatus(Material.MaterialStatus.USED);
            materialRepository.save(material);
        }
        
        sendUpdate(material);
    }

    private String generateUsedMaterialCode(String originalCode) {
        String baseCode = originalCode.replaceAll("-\\d+$", "");
        int suffix = 1;
        String newCode;
        do {
            newCode = baseCode + "-" + suffix++;
        } while (usedMaterialRepository.existsByUsedCode(newCode));
        return newCode;
    }

    private double calculateProportionalWeight(double originalGrossWeight, double originalNetWeight, double newNetWeight) {
        return (originalGrossWeight * newNetWeight) / originalNetWeight;
    }

    @Transactional
    public void updateMaterialWeight(MaterialAssignment assignment, Double updatedNetWeight) {
        if (updatedNetWeight <= 0) {
            throw new IllegalArgumentException("Updated weight must be greater than zero");
        }

        Material material = assignment.getMaterial();
        if (updatedNetWeight > material.getNetWeight()) {
            throw new IllegalArgumentException("Updated weight cannot exceed original net weight");
        }

        assignment.setUpdatedNetWeight(updatedNetWeight);
        materialAssignmentRepository.save(assignment);
        sendUpdate(material);
    }
}