package com.printshop.service;

import com.printshop.model.Weight;
import com.printshop.repository.WeightRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WeightService {
    private final WeightRepository weightRepository;

    public WeightService(WeightRepository weightRepository) {
        this.weightRepository = weightRepository;
    }

    public List<Weight> findAll() {
        return weightRepository.findAll();
    }
}