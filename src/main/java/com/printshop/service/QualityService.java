package com.printshop.service;

import com.printshop.model.Quality;
import com.printshop.repository.QualityRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QualityService {
    private final QualityRepository qualityRepository;

    public QualityService(QualityRepository qualityRepository) {
        this.qualityRepository = qualityRepository;
    }

    public List<Quality> findAll() {
        return qualityRepository.findAll();
    }
}