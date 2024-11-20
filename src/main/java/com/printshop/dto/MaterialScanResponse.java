package com.printshop.dto;

import com.printshop.model.Material;
import lombok.Data;

@Data
public class MaterialScanResponse {
    private boolean success;
    private String message;
    private Material material;

    public MaterialScanResponse(boolean success, String message, Material material) {
        this.success = success;
        this.message = message;
        this.material = material;
    }
}