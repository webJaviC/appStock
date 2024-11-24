package com.printshop.model;

import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;



import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

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
    
    @OneToMany(mappedBy = "material")
    private Set<MaterialAssignment> assignments;
    
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private Set<MaterialAssignment> assig;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MaterialStatus status = MaterialStatus.DISPONIBLE;
    
    

    public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Set<MaterialAssignment> getAssignments() {
		return assignments;
	}



	public void setAssignments(Set<MaterialAssignment> assignments) {
		this.assignments = assignments;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
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



	public MaterialStatus getStatus() {
		return status;
	}



	public void setStatus(MaterialStatus status) {
		this.status = status;
	}



	public enum MaterialStatus {
        DISPONIBLE,
        RESERVADO,
        USADO
    }
	
	@PrePersist
	protected void onCreate() {
	    if (status == null) {
	        status = MaterialStatus.DISPONIBLE;
	    }
	}
	
	  public MaterialAssignment getCurrentAssignment() {
	        if (assig != null && !assig.isEmpty()) {
	            return assig.iterator().next();
	        }
	        return null;
	    }
}
