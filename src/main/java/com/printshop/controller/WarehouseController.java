package com.printshop.controller;



import com.printshop.model.MaterialAssignment;
import com.printshop.model.WorkOrder;
import com.printshop.service.WorkOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/warehouse")
@PreAuthorize("hasRole('WAREHOUSE')")
public class WarehouseController {
    private final WorkOrderService workOrderService;

    public WarehouseController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping("/work-orders")
    public String listWorkOrders(Model model) {
        model.addAttribute("workOrders", workOrderService.findAllActive());
        return "warehouse/work-orders";
    }

    @GetMapping("/work-orders/{id}")
    public String viewWorkOrder(@PathVariable Long id, Model model) {
        WorkOrder workOrder = workOrderService.findById(id)
            .orElseThrow(() -> new RuntimeException("Work order not found"));
        model.addAttribute("workOrder", workOrder);
        return "warehouse/work-order-detail";
    }

    @PostMapping("/work-orders/{id}/close")
    public String closeWorkOrder(@PathVariable Long id) {
        workOrderService.closeWorkOrder(id);
        return "redirect:/warehouse/work-orders";
    }

    @PostMapping("/work-orders/{workOrderId}/reserve-material")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reserveMaterial(
            @PathVariable Long workOrderId,
            @RequestParam String materialCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            MaterialAssignment assignment = workOrderService.reserveMaterial(workOrderId, materialCode);
            response.put("success", true);
            response.put("message", "Material reserved successfully");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/assignments/{assignmentId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestParam(required = false) Integer orderNumber,
            @RequestParam(required = false) Double updatedNetWeight) {
        Map<String, Object> response = new HashMap<>();
        try {
            MaterialAssignment assignment = workOrderService.updateAssignment(
                assignmentId, orderNumber, updatedNetWeight);
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
