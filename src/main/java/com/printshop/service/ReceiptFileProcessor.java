package com.printshop.service;

/*
import com.printshop.model.Receipt;
import com.printshop.model.Material;
import com.printshop.model.Quality;
import com.printshop.model.Weight;
import com.printshop.repository.QualityRepository;
import com.printshop.repository.WeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;

import java.util.HashSet;
import java.util.logging.Logger;

@Service
public class ReceiptFileProcessor {
    private static final Logger logger = Logger.getLogger(ReceiptFileProcessor.class.getName());
    
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
        Receipt receipt = new Receipt();
        final Receipt finalReceipt = receipt;
        int lineNumber = 0;
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            HashSet<Material> materials = new HashSet<>();

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    if (isFirstLine) {
                        receipt = processReceiptHeader(line);
                        isFirstLine = false;
                        continue;
                    }

                    Material material = processMaterialLine(line);
                    if (material != null) {
                        materials.add(material);
                    }
                } catch (Exception e) {
                    logger.severe("Error processing line " + lineNumber + ": " + e.getMessage());
                    throw new IllegalArgumentException("Error in line " + lineNumber + ": " + e.getMessage());
                }
            }

            if (receipt == null) {
                throw new IllegalArgumentException("No valid receipt header found");
            }

            if (materials.isEmpty()) {
                throw new IllegalArgumentException("No valid materials found");
            }

            receipt.setMaterials(materials);
            materials.forEach(material -> material.setReceipt(finalReceipt));

            return receiptService.createReceipt(receipt);
        }
    }

    private Receipt processReceiptHeader(String line) {
        if (line.length() < 170) {
            throw new IllegalArgumentException("Invalid header line length");
        }

        String receiptNumber = line.substring(145, 153).trim();
        if (receiptNumber.isEmpty()) {
            throw new IllegalArgumentException("Empty receipt number");
        }

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(receiptNumber);
        receipt.setDate(parseDate(line.substring(154, 163)));
        receipt.setSupplier(line.substring(0, 1));

        
        return receipt;
    }

    private Material processMaterialLine(String line) {
        if (line.length() < 170) {
            logger.warning("Skipping invalid line: insufficient length");
            return null;
        }

        String code = line.substring(1, 11).trim();
        if (code.isEmpty()) {
            return null;
        }

        try {
            Material material = new Material();
            material.setCode(code);
            
            // Parse dimensions
            double length = parseDouble(line.substring(31, 35)) / 10.0;
            double width = parseDouble(line.substring(36, 39)) / 10.0;
            if (length <= 0 || width <= 0) {
                return null;
            }
            material.setLength(length);
            material.setWidth(width);

            // Parse weights
            double netWeight = parseDouble(line.substring(89, 93)) / 10.0;
            double grossWeight = parseDouble(line.substring(69, 73)) / 10.0;
            if (netWeight <= 0 || grossWeight <= 0) {
                return null;
            }
            material.setNetWeight(netWeight);
            material.setGrossWeight(grossWeight);

            // Set quality
            String qualityCode = line.substring(16, 17).trim();
            if (qualityCode.isEmpty()) {
                return null;
            }
            Quality quality = qualityRepository.findByName(qualityCode)
                .orElseGet(() -> {
                    Quality newQuality = new Quality();
                    newQuality.setName(qualityCode);
                    return qualityRepository.save(newQuality);
                });
            material.setQuality(quality);

            // Set weight
            String weightName = String.format("%.0fg", parseDouble(line.substring(12, 15)));
            Weight weight = weightRepository.findByName(weightName)
                .orElseGet(() -> {
                    Weight newWeight = new Weight();
                    newWeight.setName(weightName);
                    return weightRepository.save(newWeight);
                });
            material.setWeight(weight);

            material.setStatus(Material.MaterialStatus.DISPONIBLE);
            return material;
        } catch (Exception e) {
            logger.warning("Error processing material line: " + e.getMessage());
            return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return LocalDate.now();
        }

        dateStr = dateStr.replaceAll("[^0-9]", "");
        
        if (dateStr.length() < 6) {
            return LocalDate.now();
        }

        try {
            int day = Integer.parseInt(dateStr.substring(0, 2));
            int month = Integer.parseInt(dateStr.substring(2, 4));
            int year;
            
            if (dateStr.length() >= 8) {
                year = Integer.parseInt(dateStr.substring(4, 8));
            } else {
                year = Integer.parseInt(dateStr.substring(4, 6));
                year += (year >= 50) ? 1900 : 2000;
            }

            if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900) {
                return LocalDate.now();
            }

            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private double parseDouble(String value) {
        try {
            String cleanValue = value.trim().replaceAll("[^0-9.]", "");
            return cleanValue.isEmpty() ? 0.0 : Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
} */


import com.printshop.model.Receipt;
import com.printshop.model.Material;
import com.printshop.model.Quality;
import com.printshop.model.Weight;
import com.printshop.repository.QualityRepository;
import com.printshop.repository.WeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.logging.Logger;

@Service
public class ReceiptFileProcessor {
    private static final Logger logger = Logger.getLogger(ReceiptFileProcessor.class.getName());
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");
    
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
        Receipt receipt = null;
        HashSet<Material> materials = new HashSet<>();
        String firstLine = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                logger.info("Processing line " + lineNumber + ": " + line);
                
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Store first line for receipt creation
                if (firstLine == null) {
                    firstLine = line;
                }

                try {
                    Material material = processMaterialLine(line);
                    if (material != null) {
                        if (receipt == null) {
                            receipt = createReceipt(firstLine);
                        }
                        receipt.addMaterial(material);
                        materials.add(material);
                        logger.info("Added material: " + material.getCode());
                    }
                } catch (Exception e) {
                    logger.severe("Error processing line " + lineNumber + ": " + e.getMessage());
                    throw new IllegalArgumentException("Error in line " + lineNumber + ": " + e.getMessage());
                }
            }

            if (receipt == null) {
                throw new IllegalArgumentException("No valid receipt data found");
            }

            if (materials.isEmpty()) {
                throw new IllegalArgumentException("No valid materials found");
            }

            logger.info("Total materials processed: " + materials.size());
            return receiptService.createReceipt(receipt);
        }
    }

    private Receipt createReceipt(String line) {
        String receiptNumber = line.substring(145, 153).trim();
        if (receiptNumber.isEmpty()) {
            throw new IllegalArgumentException("Empty receipt number");
        }

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(receiptNumber);
        String dateStr = line.substring(153, 163).trim();
        LocalDate date = parseDate(dateStr);
        receipt.setDate(date); // Use the parsed date directly
        receipt.setSupplier(line.substring(0, 1));
        receipt.setMaterials(new HashSet<>());
        
        return receipt;
    }
    private Receipt processReceiptHeader(String line) {
        if (line.length() < 170) {
            throw new IllegalArgumentException("Invalid header line length");
        }

        String receiptNumber = line.substring(145, 153).trim();
        if (receiptNumber.isEmpty()) {
            throw new IllegalArgumentException("Empty receipt number");
        }

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(receiptNumber);
        String dateStr = line.substring(153, 163).trim();
        LocalDate date = parseDate(dateStr);
        receipt.setDate(date); // Use the parsed date directly
        receipt.setSupplier(line.substring(0, 1));
        receipt.setMaterials(new HashSet<>());
        
        return receipt;
    }

    private Material processMaterialLine(String line) {
        if (line.length() < 170) {
        	 logger.warning("Skipping invalid line: insufficient length. Line length: " + line.length());
             return null;
        }

        String code = line.substring(1, 11).trim();
        if (code.isEmpty()) {
        	 logger.warning("Skipping line: empty material code");
            return null;
        }

        try {
            Material material = new Material();
            material.setCode(code);
            
            // Parse dimensions
            double length = parseDouble(line.substring(31, 35)) / 10.0;
            double width = parseDouble(line.substring(36, 39)) / 10.0;
            if (length <= 0 || width <= 0) {
                return null;
            }
            material.setLength(length);
            material.setWidth(width);

            // Parse weights
            double netWeight = parseDouble(line.substring(89, 93));
            double grossWeight = parseDouble(line.substring(69, 73));
            if (netWeight <= 0 || grossWeight <= 0) {
                return null;
            }
            material.setNetWeight(netWeight);
            material.setGrossWeight(grossWeight);

            // Set quality
            String qualityCode = line.substring(16, 17).trim();
           /* if (qualityCode.isEmpty()) {
                return null;
            }
            Quality quality = qualityRepository.findByName(qualityCode)
                .orElseGet(() -> {
                    Quality newQuality = new Quality();
                    newQuality.setName(qualityCode);
                    return qualityRepository.save(newQuality);
                });
            material.setQuality(quality);*/
            
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

            material.setQuality(quality);

            // Set weight
            String weightName = String.format("%.0fg", parseDouble(line.substring(12, 15)));
            Weight weight = weightRepository.findByName(weightName)
                .orElseGet(() -> {
                    Weight newWeight = new Weight();
                    newWeight.setName(weightName);
                    return weightRepository.save(newWeight);
                });
            material.setWeight(weight);

            material.setStatus(Material.MaterialStatus.DISPONIBLE);
            return material;
        } catch (Exception e) {
            logger.warning("Error processing material line: " + e.getMessage());
            return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }

        // Remove any non-digit characters and pad with zeros if needed
        dateStr = dateStr.replaceAll("[^0-9]", "");
        
        // Pad with leading zeros if necessary to ensure 8 digits
        while (dateStr.length() < 8) {
            dateStr = "0" + dateStr;
        }

        try {
            // Extract day, month, and year
            String day = dateStr.substring(0, 2);
            String month = dateStr.substring(2, 4);
            String year = dateStr.substring(4);

            // Ensure year is 4 digits
            if (year.length() == 2) {
                int yearNum = Integer.parseInt(year);
                year = (yearNum >= 50 ? "19" : "20") + year;
            }

            // Reconstruct the date string in the correct format
            String formattedDate = day + month + year;
            
            return LocalDate.parse(formattedDate, DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr + ". Error: " + e.getMessage());
        }
    }
    private double parseDouble(String value) {
        try {
            String cleanValue = value.trim().replaceAll("[^0-9.]", "");
            return cleanValue.isEmpty() ? 0.0 : Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}