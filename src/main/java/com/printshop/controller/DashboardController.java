package com.printshop.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

	  @GetMapping("/admin/dashboard")
	    @PreAuthorize("hasRole('ADMIN')")
	    public String adminDashboard() {
	        return "dashboard/admin";  // Changed from admin/dashboard
	    }

	    @GetMapping("/production/dashboard")
	    @PreAuthorize("hasRole('PRODUCTION')")
	    public String productionDashboard() {
	        return "dashboard/production";  // Changed from production/dashboard
	    }

	    @GetMapping("/warehouse/dashboard")
	    @PreAuthorize("hasRole('WAREHOUSE')")
	    public String warehouseDashboard() {
	        return "dashboard/warehouse";  // Changed from warehouse/dashboard
	    }
}
