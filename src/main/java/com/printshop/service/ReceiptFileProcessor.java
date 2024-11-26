package com.printshop.service;

import com.printshop.model.Receipt;
import com.printshop.model.Material;
import com.printshop.model.Quality;
import com.printshop.model.Weight;
import com.printshop.repository.QualityRepository;
import com.printshop.repository.WeightRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;









@Service
public class ReceiptFileProcessor {
    private final ReceiptService receiptService;
    private final QualityRepository qualityRepository;
    private final WeightRepository weightRepository;

    public ReceiptFileProcessor(ReceiptService receiptService, 
                              QualityRepository qualityRepository,
                              WeightRepository weightRepository) {
        this.receiptService = receiptService;
        this.qualityRepository = qualityRepository;
        this.weightRepository = weightRepository;
    }

    @Transactional
    public Receipt processFile(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = reader.readLine();
            if (line == null || line.length() < 200) { // Validate minimum length
                throw new IllegalArgumentException("Invalid file format");
            }

            // Extract receipt number and date from first line
            String receiptNumber = line.substring(145, 153);
            String dateStr = line.substring(154, 163).trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
            LocalDate date = LocalDate.parse(dateStr, formatter);
            String supplier = line.substring(0, 1);

            // Create receipt
            Receipt receipt = new Receipt();
            receipt.setReceiptNumber(receiptNumber);
            receipt.setDate(date);
            receipt.setSupplier(supplier); // Default supplier for file imports
            receipt.setMaterials(new HashSet<>());
            

            // Process all lines
            do {
                Material material = processMaterialLine(line);
                receipt.getMaterials().add(material);
                material.setReceipt(receipt);
            } while ((line = reader.readLine()) != null);

            return receiptService.createReceipt(receipt);
        }
    }

    private Material processMaterialLine(String line) {
        Material material = new Material();

        // Extract material data
        String code = line.substring(1, 11);
        double length = Double.parseDouble(line.substring(31, 35)) / 10.0; // Convert to cm
        double width = Double.parseDouble(line.substring(36, 39)) / 10.0; // Convert to cm
        double weight = Double.parseDouble(line.substring(12, 15));
        double netWeight = Double.parseDouble(line.substring(89, 93)) ;
        double grossWeight = Double.parseDouble(line.substring(69, 73)) ;
        String qualityCode = line.substring(16,17);

        // Set material properties
        material.setCode(code);
        material.setLength(length);
        material.setWidth(width);
        material.setNetWeight(netWeight);
        material.setGrossWeight(grossWeight);

     // Get or create quality
        Quality quality;
        if ("1".equals(qualityCode)) {
            // Busca "DUPLEX" si el qualityCode es "1"
            quality = qualityRepository.findByName("DUPLEX")
                .orElseGet(() -> {
                    Quality newQuality = new Quality();
                    newQuality.setName("DUPLEX");
                    return qualityRepository.save(newQuality);
                });
        } else {
            // Busca "TRIPLEX" si el qualityCode no es "1"
            quality = qualityRepository.findByName("TRIPLEX")
                .orElseGet(() -> {
                    Quality newQuality = new Quality();
                    newQuality.setName("TRIPLEX");
                    return qualityRepository.save(newQuality);
                });
        }


        // Set quality to material
        material.setQuality(quality);

        String weightName = String.format("%.0f", weight);
        Weight weightValue = weightRepository.findByName(weightName)
            .orElseGet(() -> {
                Weight newWeight = new Weight();
                newWeight.setName(weightName);
                return weightRepository.save(newWeight);
            });

        // Set weight to material
        material.setWeight(weightValue);

        return material;
    }
}
