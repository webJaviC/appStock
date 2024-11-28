package com.printshop.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.printshop.model.Material;
import com.printshop.dto.MaterialScanResponse;
import com.printshop.service.InventoryUpdateService;
import com.printshop.service.MaterialService;

import com.printshop.service.MaterialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import com.printshop.service.MaterialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/warehouse/inventory")
public class InventoryController {
    private final MaterialService materialService;
	private final InventoryUpdateService inventoryUpdateService;

    

    public InventoryController(MaterialService materialService, InventoryUpdateService inventoryUpdateService) {
		super();
		this.materialService = materialService;
		this.inventoryUpdateService = inventoryUpdateService;
	}

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION', 'WAREHOUSE')")
    public String viewInventory(Model model) {
        List<Material> materials = materialService.findAll();
        
        // Calculate total available net weight
        double totalAvailableWeight = materials.stream()
            .filter(m -> m.getStatus() == Material.MaterialStatus.DISPONIBLE)
            .mapToDouble(Material::getNetWeight)
            .sum();

        model.addAttribute("materials", materials);
        model.addAttribute("qualities", materialService.getAllQualities());
        model.addAttribute("weights", materialService.getAllWeights());
        model.addAttribute("totalAvailableWeight", totalAvailableWeight);
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
    
   /* @GetMapping("/updates")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION', 'WAREHOUSE')")
    public SseEmitter streamInventoryUpdates() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        inventoryUpdateService.addEmitter(emitter);
        emitter.onCompletion(() -> inventoryUpdateService.removeEmitter(emitter));
        emitter.onTimeout(() -> inventoryUpdateService.removeEmitter(emitter));
        return emitter;
    }*/

    @GetMapping("/updates")
    public SseEmitter streamUpdates() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        inventoryUpdateService.addEmitter(emitter);
        
        emitter.onCompletion(() -> inventoryUpdateService.removeEmitter(emitter));
        emitter.onTimeout(() -> inventoryUpdateService.removeEmitter(emitter));
        
        return emitter;
    }
}