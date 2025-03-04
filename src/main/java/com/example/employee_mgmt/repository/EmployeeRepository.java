package com.example.employee_mgmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee_mgmt.modal.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByName(String name);
} 