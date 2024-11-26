
package com.printshop.controller;

import com.printshop.model.Receipt;
import com.printshop.model.Material;
import com.printshop.service.ReceiptService;
import com.printshop.service.ReceiptFileProcessor;
import com.printshop.service.QualityService;
import com.printshop.service.WeightService;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/production/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;
    private final ReceiptFileProcessor receiptFileProcessor;
    private final QualityService qualityService;
    private final WeightService weightService;
    
    @Value("${printshop.receipts.import-directory}")
    private String importDirectory;

    public ReceiptController(ReceiptService receiptService, 
                           ReceiptFileProcessor receiptFileProcessor,
                           QualityService qualityService,
                           WeightService weightService) {
        this.receiptService = receiptService;
        this.receiptFileProcessor = receiptFileProcessor;
        this.qualityService = qualityService;
        this.weightService = weightService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
    public String listReceipts(Model model) {
        model.addAttribute("receipts", receiptService.findAll());
        return "production/receipts/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
    public String viewReceipt(@PathVariable Long id, Model model) {
        Receipt receipt = receiptService.findById(id)
            .orElseThrow(() -> new RuntimeException("Receipt not found"));
        
        model.addAttribute("receipt", receipt);
        model.addAttribute("qualities", qualityService.findAll());
        model.addAttribute("weights", weightService.findAll());
        return "production/receipts/view";
    }
    
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return "redirect:/production/receipts";
    }

    @PostMapping("/{id}/materials/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
    public String addMaterial(@PathVariable Long id,
                            @RequestParam String code,
                            @RequestParam Long qualityId,
                            @RequestParam Long weightId,
                            @RequestParam Double grossWeight,
                            @RequestParam Double netWeight,
                            @RequestParam Double width,
                            @RequestParam Double length) {
        
        receiptService.addMaterial(id, code, qualityId, weightId, grossWeight, netWeight, width, length);
        return "redirect:/production/receipts/" + id;
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
    public String createReceiptForm(Model model) {
        model.addAttribute("receipt", new Receipt());
        return "production/receipts/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCTION')")
    public String createReceipt(@ModelAttribute Receipt receipt) {
        receiptService.createReceipt(receipt);
        return "redirect:/production/receipts";
    }

    @GetMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public String showImportForm() {
        return "production/receipts/import";
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public String importReceiptFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            Receipt receipt = receiptFileProcessor.processFile(file.getInputStream());
            return "redirect:/production/receipts/" + receipt.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Error processing file: " + e.getMessage());
            return "production/receipts/import";
        }
    }
}