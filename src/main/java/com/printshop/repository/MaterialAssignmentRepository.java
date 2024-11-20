package com.printshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.printshop.model.MaterialAssignment;

public interface MaterialAssignmentRepository extends JpaRepository<MaterialAssignment, Long> {
}
