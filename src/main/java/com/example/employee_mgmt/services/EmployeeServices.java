    package com.example.employee_mgmt.services;

    import java.util.List;
    import java.util.regex.Pattern;

    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import com.example.employee_mgmt.exception.DatabaseException;
    import com.example.employee_mgmt.exception.ServiceException;
    import com.example.employee_mgmt.modal.Employee;
    import com.example.employee_mgmt.repository.EmployeeRepository;

    @Service
    public class EmployeeServices {
        
        private final EmployeeRepository employeeRepository;
        private static final String EMPLOYEE_NOT_FOUND = "Employee with ID %d not found";
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

        public EmployeeServices(EmployeeRepository employeeRepository) {
            this.employeeRepository = employeeRepository;
        }
        // GET all employees
        public List<Employee> getAllEmployees() {
            return employeeRepository.findAll();
        }
        // CREATE a new employee
        public Employee createEmployee(Employee employee) {
            checkName(employee.getName());
            checkSalary(employee.getSalary());
            validateEmail(employee.getPersonalMail(), "Personal");
            validateEmail(employee.getOfficialMail(), "Official");

            if (employeeRepository.existsByName(employee.getName())) {
                throw new ServiceException("Employee with name " + employee.getName() + " already exists.");
            }
            try {
                return employeeRepository.save(employee);
            } catch (Exception e) {
                throw new DatabaseException("Database operation failed.");
            }
        }
        
        // DELETE an employee by ID
        @Transactional
        public void deleteEmployee(Long id) {
            if (!employeeRepository.existsById(id)) {
                throw new ServiceException(String.format(EMPLOYEE_NOT_FOUND, id));
            }
            employeeRepository.deleteById(id);
        }

        // UPDATE an existing employee
        public Employee updateEmployee(Long id, Employee updatedEmployee) {
            Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ServiceException(String.format(EMPLOYEE_NOT_FOUND, id)));
            
            if (updatedEmployee.getName() != null) {
                checkName(updatedEmployee.getName());
                existingEmployee.setName(updatedEmployee.getName());
            }
            if (updatedEmployee.getSalary() != null) {
                checkSalary(updatedEmployee.getSalary());
                existingEmployee.setSalary(updatedEmployee.getSalary());
            }
            if (updatedEmployee.getAddress() != null) {
                existingEmployee.setAddress(updatedEmployee.getAddress());
            }
            if (updatedEmployee.getPersonalMail() != null) {
                validateEmail(updatedEmployee.getPersonalMail(), "Personal");
                existingEmployee.setPersonalMail(updatedEmployee.getPersonalMail());
            }
            if (updatedEmployee.getOfficialMail() != null) {
                validateEmail(updatedEmployee.getOfficialMail(), "Official");
                existingEmployee.setOfficialMail(updatedEmployee.getOfficialMail());
            }

            try {
                return employeeRepository.save(existingEmployee);
            } catch (Exception e) {
                throw new DatabaseException("Database operation failed.");
            }
        }

        // Validate email format
        public void validateEmail(String email, String type) {
            if (email == null || email.trim().isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
                throw new ServiceException(type + " email format is invalid");
            }
        }
        

        // Check if name is valid
        public void checkName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new ServiceException("Name cannot be empty");
            }
        }

        // Validate salary
        public void checkSalary(Double salary) {
            if (salary == null || salary < 0) { 
                throw new ServiceException("Salary must be a positive value");
            }
        }
        

        public Employee existingEmployee(Long id) {
            return employeeRepository.findById(id)
                .orElseThrow(() -> new ServiceException(String.format(EMPLOYEE_NOT_FOUND, id)));
        }
        
    }
