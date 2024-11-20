package com.printshop.controller;

import com.printshop.model.Material;
import com.printshop.service.MaterialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/inventory")
public class MaterialController {
    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION', 'WAREHOUSE')")
    public String listMaterials(Model model) {
        model.addAttribute("materials", materialService.findAll());
        return "inventory/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
    public String createMaterialForm(Model model) {
        model.addAttribute("material", new Material());
        return "inventory/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
    public String createMaterial(@ModelAttribute Material material) {
        materialService.createMaterial(material);
        return "redirect:/inventory";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
    public String editMaterialForm(@PathVariable Long id, Model model) {
        materialService.findByCode(id.toString()).ifPresent(material ->
            model.addAttribute("material", material));
        return "inventory/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
    public String updateMaterial(@PathVariable Long id, @ModelAttribute Material material) {
        material.setId(id);
        materialService.updateMaterial(material);
        return "redirect:/inventory";
    }
}