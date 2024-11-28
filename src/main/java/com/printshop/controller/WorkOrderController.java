package com.printshop.controller;

import com.printshop.model.WorkOrder;
import com.printshop.service.WorkOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.printshop.dto.ReceiptMaterialSummary;

import java.util.Map;
import java.util.stream.Collectors;

import com.printshop.model.MaterialAssignment;
import com.printshop.model.Receipt;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/production/work-orders")
@PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
public class WorkOrderController {
    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping
    public String listWorkOrders(Model model) {
        model.addAttribute("workOrders", workOrderService.findAllActive());
        return "production/work-orders/list";
    }

    @GetMapping("/create")
    public String createWorkOrderForm(Model model) {
        model.addAttribute("workOrder", new WorkOrder());
        model.addAttribute("qualities", workOrderService.getAllQualities());
        model.addAttribute("weights", workOrderService.getAllWeights());

        return "production/work-orders/form";
    }

    @PostMapping("/create")
    public String createWorkOrder(@ModelAttribute WorkOrder workOrder) {
        workOrderService.createWorkOrder(workOrder);
        return "redirect:/production/work-orders";
    }

    @GetMapping("/{id}")
    public String viewWorkOrder(@PathVariable Long id, Model model) {
        workOrderService.findById(id).ifPresent(workOrder -> 
            model.addAttribute("workOrder", workOrder));
        return "production/work-orders/view";
    }

    @PostMapping("/{id}/assign-material")
    public String assignMaterial(@PathVariable Long id, 
                               @RequestParam String materialCode){
        workOrderService.reserveMaterial(id, materialCode);
        return "redirect:/production/work-orders/" + id;
    }
    
    @GetMapping("/closed")
    public String listClosedWorkOrders(Model model) {
        model.addAttribute("workOrders", workOrderService.findAllClosed());
        return "production/work-orders/closed-list";
    }

   /* @GetMapping("/closed/{id}")
    public String viewClosedWorkOrder(@PathVariable Long id, Model model) {
        WorkOrder workOrder = workOrderService.findById(id)
            .orElseThrow(() -> new RuntimeException("Work order not found"));
        model.addAttribute("workOrder", workOrder);
        return "production/work-orders/closed-detail";
    }*/
    
    @GetMapping("/closed/{id}")
    public String viewClosedWorkOrder(@PathVariable Long id, Model model) {
        WorkOrder workOrder = workOrderService.findById(id)
            .orElseThrow(() -> new RuntimeException("Hoja de Ruta no encontrada"));

        // Group assignments by receipt
        Map<Receipt, ReceiptMaterialSummary> materialsByReceipt = workOrder.getMaterialAssignments().stream()
            .collect(Collectors.groupingBy(
                assignment -> assignment.getMaterial().getReceipt(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    ReceiptMaterialSummary::new
                )
            ));

        // Calculate total weight used
        double totalWeightUsed = workOrder.getMaterialAssignments().stream()
            .mapToDouble(MaterialAssignment::getUpdatedNetWeight)
            .sum();

        model.addAttribute("workOrder", workOrder);
        model.addAttribute("materialsByReceipt", materialsByReceipt);
        model.addAttribute("totalWeightUsed", totalWeightUsed);

        return "production/work-orders/closed-detail";
    }

    @PostMapping("/{id}/close")
    public String closeWorkOrder(@PathVariable Long id) {
        workOrderService.closeWorkOrder(id);
        return "redirect:/production/work-orders";
    }
    
}
