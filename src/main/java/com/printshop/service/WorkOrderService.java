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
    private final InventoryUpdateService inventoryUpdateService;
    private final WorkOrderRepository workOrderRepository;
    private final MaterialRepository materialRepository;
    private final QualityRepository qualityRepository;
    private final WeightRepository weightRepository;
    private final MaterialAssignmentRepository materialAssignmentRepository;

    public WorkOrderService(InventoryUpdateService inventoryUpdateService,
                          WorkOrderRepository workOrderRepository,
                          MaterialRepository materialRepository,
                          QualityRepository qualityRepository,
                          WeightRepository weightRepository,
                          MaterialAssignmentRepository materialAssignmentRepository) {
        this.inventoryUpdateService = inventoryUpdateService;
        this.workOrderRepository = workOrderRepository;
        this.materialRepository = materialRepository;
        this.qualityRepository = qualityRepository;
        this.weightRepository = weightRepository;
        this.materialAssignmentRepository = materialAssignmentRepository;
    }

    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        workOrder.setDate(LocalDate.now());
        workOrder.setStatus(WorkOrderStatus.ABIERTA);
        return workOrderRepository.save(workOrder);
    }

    public Optional<WorkOrder> findById(Long id) {
        return workOrderRepository.findById(id);
    }

    public List<WorkOrder> findAllActive() {
        return workOrderRepository.findByStatus(WorkOrderStatus.ABIERTA);
    }

    public List<Quality> getAllQualities() {
        return qualityRepository.findAll();
    }

    public List<Weight> getAllWeights() {
        return weightRepository.findAll();
    }

   /*public MaterialAssignment reserveMaterial(Long workOrderId, String materialCode) {
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

        // Reserve material using inventory service
        inventoryUpdateService.reserveMaterial(material);

        MaterialAssignment assignment = new MaterialAssignment();
        assignment.setWorkOrder(workOrder);
        assignment.setMaterial(material);
        assignment.setUpdatedNetWeight(material.getNetWeight());

        workOrder.getMaterialAssignments().add(assignment);
        MaterialAssignment savedAssignment = materialAssignmentRepository.save(assignment);
        
        inventoryUpdateService.sendUpdate(material);
        return savedAssignment;
    }*/
    public MaterialAssignment reserveMaterial(Long workOrderId, String materialCode) {
        try {
            WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Hoja de Ruta no encontrada"));
            
            Material material = materialRepository.findByCode(materialCode)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con codigo: " + materialCode));
            
         // Log the current status of the material
            System.out.println("Current material status: " + material.getStatus());


            // Check if material is already assigned to this work order
            boolean alreadyAssigned = workOrder.getMaterialAssignments().stream()
                .anyMatch(a -> a.getMaterial().getCode().equals(materialCode));
            
            if (alreadyAssigned) {
                throw new RuntimeException("El material ya está asignado a esta Hoja de Ruta");
            }

            // Reserve material using inventory service
            inventoryUpdateService.reserveMaterial(material);

            MaterialAssignment assignment = new MaterialAssignment();
            assignment.setWorkOrder(workOrder);
            assignment.setMaterial(material);
            assignment.setUpdatedNetWeight(material.getNetWeight());

            workOrder.getMaterialAssignments().add(assignment);
            MaterialAssignment savedAssignment = materialAssignmentRepository.save(assignment);
            
            inventoryUpdateService.sendUpdate(material);
            return savedAssignment;

        } catch (Exception e) {
            // Log the exception (you can use a logger framework)
            System.err.println("Error En reserveMaterial: " + e.getMessage());
            throw new RuntimeException("Error al reservar material: " + e.getMessage());
        }
    }

    public MaterialAssignment updateAssignment(Long assignmentId, Integer orderNumber, Double updatedNetWeight) {
        MaterialAssignment assignment = materialAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

        if (orderNumber != null) {
            assignment.setOrderNumber(orderNumber);
            assignment.setOrdered(true);
        }

        if (updatedNetWeight != null) {
            inventoryUpdateService.updateMaterialWeight(assignment, updatedNetWeight);
        }

        return materialAssignmentRepository.save(assignment);
    }

    public void removeAssignment(Long assignmentId) {
        MaterialAssignment assignment = materialAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

        Material material = assignment.getMaterial();
        inventoryUpdateService.releaseMaterial(material);

        WorkOrder workOrder = assignment.getWorkOrder();
        workOrder.getMaterialAssignments().remove(assignment);
        workOrderRepository.save(workOrder);
        
        materialAssignmentRepository.delete(assignment);
        inventoryUpdateService.sendUpdate(material);
    }

    public void closeWorkOrder(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hoja de Ruta no encontrada"));

        if (workOrder.getMaterialAssignments().isEmpty()) {
            throw new RuntimeException("No se puede cerrar la orden de trabajo: No hay materiales asignados");
        }

        boolean allMaterialsOrdered = workOrder.getMaterialAssignments().stream()
            .allMatch(MaterialAssignment::isOrdered);
            
        if (!allMaterialsOrdered) {
            throw new RuntimeException("No se puede cerrar la orden de trabajo: Algunos materiales no están ordenados");
        }

        // Process each material assignment using inventory service
        workOrder.getMaterialAssignments().forEach(assignment -> 
            inventoryUpdateService.processUsedMaterial(assignment, workOrder));
        
        workOrder.setStatus(WorkOrderStatus.CERRADA);
        workOrderRepository.save(workOrder);
    }
    
    public List<WorkOrder> findAllClosed() {
        return workOrderRepository.findByStatus(WorkOrderStatus.CERRADA);
    }

    public List<WorkOrder> findAll() {
        return workOrderRepository.findAll();
    }
}