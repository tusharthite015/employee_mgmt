// package com.example.employee_mgmt.controller;

// import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.employee_mgmt.exception.ErrorResponse;
// import com.example.employee_mgmt.exception.ServiceException;
// import com.example.employee_mgmt.modal.Employee;
// import com.example.employee_mgmt.services.EmployeeServices;

// @RestController
// @RequestMapping("/employee")
// public class EmployeeController {
//     private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
//     private final EmployeeServices employeeService;
//     // Constructor
//     public EmployeeController(EmployeeServices employeeService) {
//         this.employeeService = employeeService;
//     }
//     // GET 
//     @GetMapping
//     public ResponseEntity<List<Employee>> getAllEmployees() {
//         List<Employee> employees = employeeService.getAllEmployees();
//         return ResponseEntity.ok(employees);
//     }
    
//     @PostMapping
//     public ResponseEntity<Object> addEmployee(@RequestBody Employee newEmployee) {
//         try {
//             Employee createdEmployee = employeeService.createEmployee(newEmployee);
//             return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
//         } catch (ServiceException e) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
//         }
//     }
    
//     // DELETE 
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
//         try {
//             employeeService.deleteEmployee(id);
//             return ResponseEntity.noContent().build(); 
//         } catch (ServiceException e) {
//             return ResponseEntity.notFound().build();
//         }
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
//         logger.info("Updating employee with id: {}", id);
//         try {
//             Employee updated = employeeService.updateEmployee(id, updatedEmployee);
//             logger.info("Employee updated successfully");
//             return ResponseEntity.ok(updated);
//         } catch (ServiceException e) {  // Catch ServiceException and return 404
//             logger.error("Employee not found: {}", e.getMessage());
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//         } catch (Exception e) {
//             logger.error("Error updating employee: {}", e.getMessage());
//             return ResponseEntity.badRequest().build();
//         }
//     }
    
// }





package com.example.employee_mgmt.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee_mgmt.exception.ErrorResponse;
import com.example.employee_mgmt.exception.ServiceException;
import com.example.employee_mgmt.modal.Employee;
import com.example.employee_mgmt.services.EmployeeServices;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeServices employeeService;

    public EmployeeController(EmployeeServices employeeService) {
        this.employeeService = employeeService;
    }

    // **Allow only authenticated users**
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> addEmployee(@RequestBody Employee newEmployee) {
        try {
            Employee createdEmployee = employeeService.createEmployee(newEmployee);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    //**Allow only 'ADMIN' to delete**
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (ServiceException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
        logger.info("Updating employee with id: {}", id);
        try {
            Employee updated = employeeService.updateEmployee(id, updatedEmployee);
            logger.info("Employee updated successfully");
            return ResponseEntity.ok(updated);
        } catch (ServiceException e) {
            logger.error("Employee not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error updating employee: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
