package com.printshop.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "used_materials")
public class UsedMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalCode;

    @Column(nullable = false, unique = true)
    private String usedCode;

    @ManyToOne
    @JoinColumn(name = "quality_id", nullable = false)
    private Quality quality;

    @ManyToOne
    @JoinColumn(name = "weight_id", nullable = false)
    private Weight weight;

    private Double grossWeight;
    private Double netWeight;
    private Double width;
    private Double length;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @ManyToOne
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    private LocalDateTime usedDate;

    @PrePersist
    protected void onCreate() {
        usedDate = LocalDateTime.now();
        
        
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOriginalCode() {
		return originalCode;
	}

	public void setOriginalCode(String originalCode) {
		this.originalCode = originalCode;
	}

	public String getUsedCode() {
		return usedCode;
	}

	public void setUsedCode(String usedCode) {
		this.usedCode = usedCode;
	}

	public Quality getQuality() {
		return quality;
	}

	public void setQuality(Quality quality) {
		this.quality = quality;
	}

	public Weight getWeight() {
		return weight;
	}

	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	public Double getGrossWeight() {
		return grossWeight;
	}

	public void setGrossWeight(Double grossWeight) {
		this.grossWeight = grossWeight;
	}

	public Double getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(Double netWeight) {
		this.netWeight = netWeight;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public WorkOrder getWorkOrder() {
		return workOrder;
	}

	public void setWorkOrder(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}

	public LocalDateTime getUsedDate() {
		return usedDate;
	}

	public void setUsedDate(LocalDateTime usedDate) {
		this.usedDate = usedDate;
	}
}
