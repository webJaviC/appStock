package com.printshop.dto;

import com.printshop.model.MaterialAssignment;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ReceiptMaterialSummary {
    private final List<MaterialAssignment> assignments;
    private final double totalWeight;

    public ReceiptMaterialSummary(List<MaterialAssignment> assignments) {
        this.assignments = assignments;
        this.totalWeight = calculateTotalWeight();
    }

    private double calculateTotalWeight() {
        return assignments.stream()
            .mapToDouble(MaterialAssignment::getUpdatedNetWeight)
            .sum();
    }

    public List<MaterialAssignment> getAssignments() {
        return assignments;
    }

    public double getTotalWeight() {
        return totalWeight;
    }
}
