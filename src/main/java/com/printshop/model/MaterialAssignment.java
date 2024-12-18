package com.printshop.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;



@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "material_assignments")
public class MaterialAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;
    
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    private Integer orderNumber;
    private Double updatedNetWeight;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignmentDate;
    
    
    private boolean ordered = false;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MaterialStatus status = MaterialStatus.DISPONIBLE;

    @PrePersist
    protected void onCreate() {
        assignmentDate = LocalDateTime.now();
        
        
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WorkOrder getWorkOrder() {
		return workOrder;
	}

	public void setWorkOrder(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Double getUpdatedNetWeight() {
		return updatedNetWeight;
	}

	public void setUpdatedNetWeight(Double updatedNetWeight) {
		this.updatedNetWeight = updatedNetWeight;
	}

	public LocalDateTime getAssignmentDate() {
		return assignmentDate;
	}

	public void setAssignmentDate(LocalDateTime assignmentDate) {
		this.assignmentDate = assignmentDate;
	}

	public boolean isOrdered() {
		return ordered;
	}

	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}
	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof MaterialAssignment)) return false;
	        MaterialAssignment that = (MaterialAssignment) o;
	        return Objects.equals(id, that.id);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(id);
	    }
}