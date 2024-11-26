package com.printshop.service;

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
        int lineNumber = 0;
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (line.length() < 132) {
                    logger.warning("Línea " + lineNumber + " inválida (longitud insuficiente): " + line);
                    continue;
                }

                try {
                    if (isFirstLine) {
                        receipt = createReceipt(line);
                        receipt.setMaterials(new HashSet<>());
                        isFirstLine = false;
                    }

                    if (receipt != null) {
                        Material material = processMaterialLine(line);
                        if (material != null) {
                            material.setReceipt(receipt);
                            receipt.getMaterials().add(material);
                        }
                    }
                } catch (Exception e) {
                    logger.severe("Error procesando línea " + lineNumber + ": " + e.getMessage());
                    throw new IllegalArgumentException("Error en línea " + lineNumber + ": " + e.getMessage());
                }
            }

            if (receipt == null || receipt.getMaterials().isEmpty()) {
                throw new IllegalArgumentException("No se encontraron datos válidos en el archivo");
            }

            return receiptService.createReceipt(receipt);
        }
    }

    private Receipt createReceipt(String line) {
        String receiptNumber = line.substring(145, 153).trim();
        if (receiptNumber.isEmpty()) {
            throw new IllegalArgumentException("Número de remito vacío");
        }

        LocalDate date = parseDate(line.substring(154, 163));
        if (date == null) {
            date = LocalDate.now();
        }

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(receiptNumber);
        receipt.setDate(date);
        receipt.setSupplier(line.substring(0, 1));
        
        return receipt;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        dateStr = dateStr.replaceAll("[^0-9]", "");
        
        if (dateStr.length() < 6) {
            return null;
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
                return null;
            }

            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            return null;
        }
    }

    private Material processMaterialLine(String line) {
        try {
            String code = line.substring(1, 11).trim();
            if (code.isEmpty()) {
                return null;
            }

            double length = parseDouble(line.substring(31, 35)) / 10.0;
            double width = parseDouble(line.substring(36, 39)) / 10.0;
            if (length <= 0 || width <= 0) {
                return null;
            }

            double netWeight = parseDouble(line.substring(89, 93)) ;
            double grossWeight = parseDouble(line.substring(69, 73));
            if (netWeight <= 0 || grossWeight <= 0) {
                return null;
            }

            String qualityCode = line.substring(16,17).trim();
            if (qualityCode.isEmpty()) {
                return null;
            }

            Material material = new Material();
            material.setCode(code);
            material.setLength(length);
            material.setWidth(width);
            material.setNetWeight(netWeight);
            material.setGrossWeight(grossWeight);
            material.setStatus(Material.MaterialStatus.DISPONIBLE);

            
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

            String weightName = String.format("%.0fg", parseDouble(line.substring(12,15)));
            Weight weight = weightRepository.findByName(weightName)
                .orElseGet(() -> {
                    Weight newWeight = new Weight();
                    newWeight.setName(weightName);
                    return weightRepository.save(newWeight);
                });
            material.setWeight(weight);

            return material;
        } catch (Exception e) {
            logger.warning("Error procesando material: " + e.getMessage());
            return null;
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