package com.printshop.service;

import com.printshop.model.WorkOrder;
import com.printshop.model.Material;
import com.printshop.model.MaterialAssignment;
import com.printshop.model.Quality;
import com.printshop.model.Weight;
import com.printshop.model.WorkOrderStatus;
import com.printshop.repository.WorkOrderRepository;
import com.printshop.repository.MaterialAssignmentRepository;
import com.printshop.repository.MaterialRepository;
import com.printshop.repository.QualityRepository;
import com.printshop.repository.WeightRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkOrderService {
    private final WorkOrderRepository workOrderRepository;
    private final MaterialRepository materialRepository;
    private final QualityRepository qualityRepository;
    private final WeightRepository weightRepository;
    private final MaterialAssignmentRepository materialAssignmentRepository;

    public WorkOrderService(WorkOrderRepository workOrderRepository, 
                          MaterialRepository materialRepository,
                          QualityRepository qualityRepository,
                          WeightRepository weightRepository,
                          MaterialAssignmentRepository materialAssignmentRepository) {
        this.workOrderRepository = workOrderRepository;
        this.materialRepository = materialRepository;
        this.qualityRepository = qualityRepository;
        this.weightRepository = weightRepository;
        this.materialAssignmentRepository = materialAssignmentRepository;
    }

    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        workOrder.setDate(LocalDate.now());
        workOrder.setStatus(WorkOrderStatus.OPEN);
        return workOrderRepository.save(workOrder);
    }

    public Optional<WorkOrder> findById(Long id) {
        return workOrderRepository.findById(id);
    }

    public List<WorkOrder> findAllActive() {
        return workOrderRepository.findByStatus(WorkOrderStatus.OPEN);
    }

    public List<Quality> getAllQualities() {
        return qualityRepository.findAll();
    }

    public List<Weight> getAllWeights() {
        return weightRepository.findAll();
    }

    public MaterialAssignment reserveMaterial(Long workOrderId, String materialCode) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
            .orElseThrow(() -> new RuntimeException("Work order not found"));
        
        Material material = materialRepository.findByCode(materialCode)
            .orElseThrow(() -> new RuntimeException("Material not found with code: " + materialCode));

        // Check if material is already assigned to this work order
        boolean alreadyAssigned = workOrder.getMaterialAssignments().stream()
            .anyMatch(a -> a.getMaterial().getCode().equals(materialCode));
        
        if (alreadyAssigned) {
            throw new RuntimeException("Material is already assigned to this work order");
        }

        // Check material status and provide specific messages
        switch (material.getStatus()) {
            case RESERVED:
                throw new RuntimeException("Material is already reserved for another work order");
            case USED:
                throw new RuntimeException("Material has already been used");
            case AVAILABLE:
                break;
            default:
                throw new RuntimeException("Invalid material status");
        }

        MaterialAssignment assignment = new MaterialAssignment();
        assignment.setWorkOrder(workOrder);
        assignment.setMaterial(material);
        assignment.setUpdatedNetWeight(material.getNetWeight());

        material.setStatus(Material.MaterialStatus.RESERVED);
        materialRepository.save(material);

        workOrder.getMaterialAssignments().add(assignment);
        return materialAssignmentRepository.save(assignment);
    }
    
    

    public MaterialAssignment updateAssignment(Long assignmentId, Integer orderNumber, Double updatedNetWeight) {
        MaterialAssignment assignment = materialAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (orderNumber != null) {
            assignment.setOrderNumber(orderNumber);
            assignment.setOrdered(true);
        }

        if (updatedNetWeight != null) {
            if (updatedNetWeight <= 0) {
                throw new RuntimeException("Updated net weight must be greater than 0");
            }
            if (updatedNetWeight > assignment.getMaterial().getNetWeight()) {
                throw new RuntimeException("Updated net weight cannot be greater than original net weight");
            }
            assignment.setUpdatedNetWeight(updatedNetWeight);
        }

        return materialAssignmentRepository.save(assignment);
    }

    public void removeAssignment(Long assignmentId) {
        MaterialAssignment assignment = materialAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Material material = assignment.getMaterial();
        material.setStatus(Material.MaterialStatus.AVAILABLE);
        materialRepository.save(material);

        WorkOrder workOrder = assignment.getWorkOrder();
        workOrder.getMaterialAssignments().remove(assignment);
        workOrderRepository.save(workOrder);
        
        materialAssignmentRepository.delete(assignment);
    }

    public void closeWorkOrder(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Work order not found"));

        if (workOrder.getMaterialAssignments().isEmpty()) {
            throw new RuntimeException("Cannot close work order: No materials assigned");
        }

        boolean allMaterialsOrdered = workOrder.getMaterialAssignments().stream()
            .allMatch(MaterialAssignment::isOrdered);
            
        if (!allMaterialsOrdered) {
            throw new RuntimeException("Cannot close work order: Some materials are not ordered");
        }

        workOrder.getMaterialAssignments().forEach(assignment -> {
            Material material = assignment.getMaterial();
            material.setStatus(Material.MaterialStatus.USED);
            materialRepository.save(material);
        });
        
        workOrder.setStatus(WorkOrderStatus.CLOSED);
        workOrderRepository.save(workOrder);
    }
    
    public List<WorkOrder> findAllClosed() {
        return workOrderRepository.findByStatus(WorkOrderStatus.CLOSED);
    }

    public List<WorkOrder> findAll() {
        return workOrderRepository.findAll();
    }

}