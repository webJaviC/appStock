package com.printshop.repository;

import com.printshop.model.WorkOrder;
import com.printshop.model.WorkOrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByStatus(WorkOrderStatus status);
    boolean existsByRouteNumber(String routeNumber);
}