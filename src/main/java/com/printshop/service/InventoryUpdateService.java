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
import com.printshop.repository.MaterialAssignmentRepository;


@Service
public class InventoryUpdateService {
 private final Set<SseEmitter> emitters = Collections.synchronizedSet(new HashSet<>());
private final MaterialAssignmentRepository materialAssignmentRepository;



 public InventoryUpdateService(MaterialAssignmentRepository materialAssignmentRepository) {
	super();
	this.materialAssignmentRepository = materialAssignmentRepository;
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
 }
}

