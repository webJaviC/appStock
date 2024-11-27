package com.printshop.service;
/*
import com.printshop.model.Receipt;
import com.printshop.model.Material;
import com.printshop.model.Quality;
import com.printshop.model.Weight;
import com.printshop.repository.ReceiptRepository;
import com.printshop.repository.QualityRepository;
import com.printshop.repository.WeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final QualityRepository qualityRepository;
    private final WeightRepository weightRepository;

    public ReceiptService(ReceiptRepository receiptRepository,
                         QualityRepository qualityRepository,
                         WeightRepository weightRepository) {
        this.receiptRepository = receiptRepository;
        this.qualityRepository = qualityRepository;
        this.weightRepository = weightRepository;
    }

    public Receipt createReceipt(Receipt receipt) {
    	
    	System.out.println("Fecha recibida: " + receipt.getDate()); 
        if (receiptRepository.existsByReceiptNumber(receipt.getReceiptNumber())) {
            throw new RuntimeException("Ya existe el recibo con este número");
        }
        
        
       // receipt.setDate(LocalDate.now());
        
        return receiptRepository.save(receipt);
    }

    public Material addMaterial(Long receiptId, String code, Long qualityId, Long weightId,
                              Double grossWeight, Double netWeight, Double width, Double length) {
        Receipt receipt = receiptRepository.findById(receiptId)
            .orElseThrow(() -> new RuntimeException("Recibo no encontrado"));

        Quality quality = qualityRepository.findById(qualityId)
            .orElseThrow(() -> new RuntimeException("Calidad no encontrado"));

        Weight weight = weightRepository.findById(weightId)
            .orElseThrow(() -> new RuntimeException("Gramaje no encontrado"));

        Material material = new Material();
        material.setCode(code);
        material.setQuality(quality);
        material.setWeight(weight);
        material.setGrossWeight(grossWeight);
        material.setNetWeight(netWeight);
        material.setWidth(width);
        material.setLength(length);
        material.setReceipt(receipt);

        receipt.getMaterials().add(material);
        receiptRepository.save(receipt);

        return material;
    }
    
    public void deleteReceipt(Long id) {
        Receipt receipt = receiptRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recibo no encontrado"));
        receiptRepository.delete(receipt);
    }

    public Optional<Receipt> findByReceiptNumber(String receiptNumber) {
        return receiptRepository.findByReceiptNumber(receiptNumber);
    }

    public Optional<Receipt> findById(Long id) {
        return receiptRepository.findById(id);
    }

    public List<Receipt> findAll() {
        return receiptRepository.findAll();
    }

    public Receipt updateReceipt(Receipt receipt) {
        if (!receiptRepository.existsById(receipt.getId())) {
            throw new RuntimeException("Recibo no encontrado");
        }
        return receiptRepository.save(receipt);
    }
}*/

import com.printshop.model.Receipt;
import com.printshop.model.Material;
import com.printshop.model.Quality;
import com.printshop.model.Weight;
import com.printshop.repository.ReceiptRepository;
import com.printshop.repository.QualityRepository;
import com.printshop.repository.WeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final QualityRepository qualityRepository;
    private final WeightRepository weightRepository;

    public ReceiptService(ReceiptRepository receiptRepository,
                         QualityRepository qualityRepository,
                         WeightRepository weightRepository) {
        this.receiptRepository = receiptRepository;
        this.qualityRepository = qualityRepository;
        this.weightRepository = weightRepository;
    }

    public Receipt createReceipt(Receipt receipt) {
        if (receiptRepository.existsByReceiptNumber(receipt.getReceiptNumber())) {
            throw new RuntimeException("Receipt with this number already exists");
        }
        
        // Ensure date is set
        if (receipt.getDate() == null) {
            receipt.setDate(LocalDate.now());
        }

        // Save the receipt with its materials in a single transaction
        return receiptRepository.save(receipt);
    }

    public Material addMaterial(Long receiptId, String code, Long qualityId, Long weightId,
                              Double grossWeight, Double netWeight, Double width, Double length) {
        Receipt receipt = receiptRepository.findById(receiptId)
            .orElseThrow(() -> new RuntimeException("Receipt not found"));

        Quality quality = qualityRepository.findById(qualityId)
            .orElseThrow(() -> new RuntimeException("Quality not found"));

        Weight weight = weightRepository.findById(weightId)
            .orElseThrow(() -> new RuntimeException("Weight not found"));

        Material material = new Material();
        material.setCode(code);
        material.setQuality(quality);
        material.setWeight(weight);
        material.setGrossWeight(grossWeight);
        material.setNetWeight(netWeight);
        material.setWidth(width);
        material.setLength(length);
        material.setReceipt(receipt);

        receipt.getMaterials().add(material);
        receiptRepository.save(receipt);

        return material;
    }

    public void deleteReceipt(Long id) {
        Receipt receipt = receiptRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Receipt not found"));
        receiptRepository.delete(receipt);
    }

    public Optional<Receipt> findByReceiptNumber(String receiptNumber) {
        return receiptRepository.findByReceiptNumber(receiptNumber);
    }

    public Optional<Receipt> findById(Long id) {
        return receiptRepository.findById(id);
    }

    public List<Receipt> findAll() {
        return receiptRepository.findAll();
    }

    public Receipt updateReceipt(Receipt receipt) {
        if (!receiptRepository.existsById(receipt.getId())) {
            throw new RuntimeException("Receipt not found");
        }
        return receiptRepository.save(receipt);
    }
}