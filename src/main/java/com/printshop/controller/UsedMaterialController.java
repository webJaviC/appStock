package com.printshop.controller;

import com.printshop.model.UsedMaterial;
import com.printshop.service.UsedMaterialService;
import com.printshop.service.QualityService;
import com.printshop.service.WeightService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/warehouse/used-materials")
@PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE')")
public class UsedMaterialController {
    private final UsedMaterialService usedMaterialService;
    private final QualityService qualityService;
    private final WeightService weightService;

    public UsedMaterialController(UsedMaterialService usedMaterialService,
                                QualityService qualityService,
                                WeightService weightService) {
        this.usedMaterialService = usedMaterialService;
        this.qualityService = qualityService;
        this.weightService = weightService;
    }

    @GetMapping
    public String listUsedMaterials(Model model) {
        model.addAttribute("usedMaterials", usedMaterialService.findAll());
        model.addAttribute("qualities", qualityService.findAll());
        model.addAttribute("weights", weightService.findAll());
        return "warehouse/used-materials";
    }
}