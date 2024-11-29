package com.printshop.controller;

import com.printshop.model.WorkOrder;
import com.printshop.service.WorkOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/warehouse/material-assignment")
@PreAuthorize("hasRole('WAREHOUSE')")
public class MaterialAssignmentController {
    private final WorkOrderService workOrderService;

    public MaterialAssignmentController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping("/{workOrderId}")
    public String showAssignmentPage(@PathVariable Long workOrderId, Model model) {
        WorkOrder workOrder = workOrderService.findById(workOrderId)
            .orElseThrow(() -> new RuntimeException("Work order not found"));
        model.addAttribute("workOrder", workOrder);
        return "warehouse/material-assignment";
    }

    @PostMapping("/{workOrderId}/assign")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> assignMaterial(
            @PathVariable Long workOrderId,
            @RequestParam String materialCode,
            @RequestParam(required = false) Integer orderNumber,
            @RequestParam(required = false) Double updatedNetWeight) {
        Map<String, Object> response = new HashMap<>();
        try {
            var assignment = workOrderService.reserveMaterial(workOrderId, materialCode);
            
            if (orderNumber != null || updatedNetWeight != null) {
                assignment = workOrderService.updateAssignment(assignment.getId(), orderNumber, updatedNetWeight);
            }
            
            response.put("success", true);
            response.put("message", "Material assigned successfully");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/assignments/{assignmentId}/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestParam(required = false) Integer orderNumber,
            @RequestParam(required = false) Double updatedNetWeight) {
        Map<String, Object> response = new HashMap<>();
        try {
            var assignment = workOrderService.updateAssignment(assignmentId, orderNumber, updatedNetWeight);
            response.put("success", true);
            response.put("message", "Assignment updated successfully");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/assignments/{assignmentId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeAssignment(@PathVariable Long assignmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            workOrderService.removeAssignment(assignmentId);
            response.put("success", true);
            response.put("message", "Assignment removed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
