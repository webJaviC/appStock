package com.printshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.printshop.dto.MaterialScanResponse;
import com.printshop.service.MaterialService;

@Controller
@RequestMapping("/warehouse/inventory")
public class InventoryController {
    private final MaterialService materialService;

    public InventoryController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION', 'WAREHOUSE')")
    public String viewInventory(Model model) {
        model.addAttribute("materials", materialService.findAll());
        model.addAttribute("qualities", materialService.getAllQualities());
        model.addAttribute("weights", materialService.getAllWeights());
        return "warehouse/inventory";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION', 'WAREHOUSE')")
    public String viewMaterial(@PathVariable Long id, Model model) {
        model.addAttribute("material", materialService.findById(id));
        return "warehouse/inventory-detail";
    }

    @PostMapping("/api/scan")
    @PreAuthorize("hasRole('WAREHOUSE')")
    @ResponseBody
    public ResponseEntity<MaterialScanResponse> scanMaterial(@RequestParam String code) {
        return materialService.findByCode(code)
            .map(material -> ResponseEntity.ok(new MaterialScanResponse(true, "Material found", material)))
            .orElse(ResponseEntity.ok(new MaterialScanResponse(false, "Material not found", null)));
    }
}