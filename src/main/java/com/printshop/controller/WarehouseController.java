package com.printshop.controller;

import com.printshop.model.MaterialAssignment;
import com.printshop.model.WorkOrder;
import com.printshop.service.WorkOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
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
            .orElseThrow(() -> new RuntimeException("Hoja de Ruta no encontrada"));
        model.addAttribute("workOrder", workOrder);
        return "warehouse/work-order-detail";
    }

    @PostMapping("/work-orders/{id}/close")
    public String closeWorkOrder(@PathVariable Long id) {
        workOrderService.closeWorkOrder(id);
        return "redirect:/warehouse/work-orders";
    }

   /* @PostMapping("/work-orders/{workOrderId}/reserve-material")
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
    }*/
    
  /*  @PostMapping("/work-orders/{workOrderId}/reserve-material")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reserveMaterial(
            @PathVariable Long workOrderId,
            @RequestParam String materialCode) {
        Map<String, Object> response = new HashMap<>();
        
        if (materialCode == null || materialCode.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "El código de material no puede estar vacío");
            return ResponseEntity.badRequest().body(response); // 400 Bad Request
        }
        
        try {
            MaterialAssignment assignment = workOrderService.reserveMaterial(workOrderId, materialCode);
            response.put("success", true);
            response.put("message", "Material reservado con éxito");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al reservar el material: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    */
    
    @PostMapping("/work-orders/{workOrderId}/reserve-material")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reserveMaterial(
            @PathVariable Long workOrderId,
            @RequestParam String materialCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            MaterialAssignment assignment = workOrderService.reserveMaterial(workOrderId, materialCode);
            response.put("success", true);
            response.put("message", "Material reservado con éxito");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
       } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al reservar el material: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/work-orders/{workOrderId}/assign-material")
    public ResponseEntity<Map<String, Object>> assignMaterial(
            @PathVariable Long workOrderId,
            @RequestParam String materialCode,
            @RequestParam(required = false) Integer orderNumber,
            @RequestParam(required = false) Double updatedNetWeight) {
        Map<String, Object> response = new HashMap<>();
        try {
            MaterialAssignment assignment = workOrderService.reserveMaterial(workOrderId, materialCode);
            
            if (orderNumber != null || updatedNetWeight != null) {
                assignment = workOrderService.updateAssignment(assignment.getId(), orderNumber, updatedNetWeight);
            }
            
            response.put("success", true);
            response.put("message", "Material Asignado con exito");
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
            response.put("message", "Tarea actualizada exitosamente");
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
            response.put("message", "Tarea eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
