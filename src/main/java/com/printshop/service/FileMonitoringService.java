package com.printshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.*;
import java.util.logging.Logger;

@Service
public class FileMonitoringService {
    private static final Logger logger = Logger.getLogger(FileMonitoringService.class.getName());
    
    @Value("${printshop.receipts.import-directory:/tmp/receipts}")
    private String importDirectory;
    
    private final ReceiptFileProcessor receiptFileProcessor;

    public FileMonitoringService(ReceiptFileProcessor receiptFileProcessor) {
        this.receiptFileProcessor = receiptFileProcessor;
    }

    @PostConstruct
    public void init() {
        createImportDirectoryIfNotExists();
    }

    private void createImportDirectoryIfNotExists() {
        File directory = new File(importDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
            logger.info("Created import directory: " + importDirectory);
        }
    }

    @Scheduled(fixedDelay = 5000) // Check every 5 seconds
    public void checkForNewFiles() {
        File directory = new File(importDirectory);
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        
        if (files != null) {
            for (File file : files) {
                processFile(file);
            }
        }
    }

    private void processFile(File file) {
        try {
            logger.info("Processing file: " + file.getName());
            
            // Process the file
            try (FileInputStream fis = new FileInputStream(file)) {
                receiptFileProcessor.processFile(fis);
            }
            
            // Move to processed directory
            File processedDir = new File(importDirectory, "processed");
            processedDir.mkdirs();
            
            Files.move(file.toPath(),
                      new File(processedDir, file.getName()).toPath(),
                      StandardCopyOption.REPLACE_EXISTING);
            
            logger.info("Successfully processed file: " + file.getName());
        } catch (Exception e) {
            logger.severe("Error processing file " + file.getName() + ": " + e.getMessage());
            
            // Move to error directory
            try {
                File errorDir = new File(importDirectory, "error");
                errorDir.mkdirs();
                
                Files.move(file.toPath(),
                          new File(errorDir, file.getName()).toPath(),
                          StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception moveError) {
                logger.severe("Error moving failed file: " + moveError.getMessage());
            }
        }
    }
}