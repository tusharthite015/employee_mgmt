package com.example.employee_mgmt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.employee_mgmt.controller.EmployeeController;
import com.example.employee_mgmt.exception.DatabaseException;
import com.example.employee_mgmt.exception.ErrorResponse;
import com.example.employee_mgmt.exception.ServiceException;
import com.example.employee_mgmt.modal.Employee;
import com.example.employee_mgmt.repository.EmployeeRepository;
import com.example.employee_mgmt.services.EmployeeServices;

@ExtendWith(MockitoExtension.class)
class EmployeeMgmtApplicationTests {

    @Mock
    private EmployeeServices employeeServices;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks    
    private EmployeeController employeeController;

    private Employee testEmployee;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee("test user ", 50000.0, "anc", "abc.personal@example.com", "abc.official@example.com");
        testEmployee.setId(1L);
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() throws Exception {
        // Arrange
        List<Employee> mockEmployees = Arrays.asList(
            new Employee("Alice", 60000.0, "Address 1", "alice@example.com", "alice.official@example.com"),
            new Employee("Bob", 70000.0, "Address 2", "bob@example.com", "bob.official@example.com")
        );
        when(employeeServices.getAllEmployees()).thenReturn(mockEmployees);
        // Act & Assert
        mockMvc.perform(get("/employee")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.size()").value(2)) 
                .andExpect(jsonPath("$[0].name").value("Alice")) 
                .andExpect(jsonPath("$[1].name").value("Bob"));

        // Verify service method was called once
        verify(employeeServices, times(1)).getAllEmployees();
    }
    @Test
    void createEmployee_ShouldThrowExceptionWhenEmployeeExists() {
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        Employee employee = new Employee("John Doe", 50000.0, "Address 1", "john@example.com", "john.official@example.com");
        employee.setId(1L);

        when(employeeRepository.existsByName("John Doe")).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> employeeServicesTest.createEmployee(employee));

        assertEquals("Employee with name John Doe already exists.", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }
    @Test
    void createEmployee_ShouldReturnCreatedEmployee() {
        Employee employee = new Employee("John Doe", 50000.0, "Address 1", "john@example.com", "john@example.com");
        employee.setId(1L);
    
        when(employeeRepository.existsByName(employee.getName())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
    
        EmployeeServices realEmployeeService = new EmployeeServices(employeeRepository);
    
        Employee createdEmployee = realEmployeeService.createEmployee(employee);
        assertNotNull(createdEmployee); 
        assertEquals(employee.getName(), createdEmployee.getName());
        assertEquals(employee.getSalary(), createdEmployee.getSalary());
        assertEquals(employee.getPersonalMail(), createdEmployee.getPersonalMail());
    
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
    

    @Test
    void createEmployee_ShouldSaveEmployee() {
        when(employeeServices.createEmployee(any(Employee.class))).thenReturn(testEmployee);

        ResponseEntity<?> response = employeeController.addEmployee(testEmployee);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testEmployee, response.getBody());
    }

    @Test
    void createEmployee_ShouldThrowExceptionForInvalidPersonalEmail() {
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        Employee employee = new Employee("John Doe", 50000.0, "Address 1", "johnofficial@example.com", "johnofficial@example.com");
        employee.setId(1L);

        ServiceException exception = assertThrows(ServiceException.class, () -> employeeServicesTest.createEmployee(employee));
        assertEquals("Personal email format is invalid", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }
    @Test
    void createEmployee_ShouldThrowExceptionForInvalidOfficialEmail() {
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        Employee employee = new Employee("John Doe", 50000.0, "Address 1", "john@example.com", "invalid-email");
        employee.setId(1L);

        ServiceException exception = assertThrows(ServiceException.class, () -> employeeServicesTest.createEmployee(employee));

        assertEquals("Official email format is invalid", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_ShouldThrowExceptionForEmptyName() {
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        Employee employee = new Employee("", 50000.0, "Address 1", "john@example.com", "john.official@example.com");
        employee.setId(1L);

        ServiceException exception = assertThrows(ServiceException.class, () -> employeeServicesTest.createEmployee(employee));

        assertEquals("Name cannot be empty", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_ShouldThrowExceptionForNullName() {
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        Employee employee = new Employee(null, 50000.0, "Address 1", "john@example.com", "john.official@example.com");
        employee.setId(1L);

        ServiceException exception = assertThrows(ServiceException.class, () -> employeeServicesTest.createEmployee(employee));

        assertEquals("Name cannot be empty", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_ShouldThrowExceptionForNegativeSalary() {
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        Employee employee = new Employee("John Doe", -1000.0, "Address 1", "john@example.com", "john.official@example.com");
        employee.setId(1L);

        ServiceException exception = assertThrows(ServiceException.class, () -> employeeServicesTest.createEmployee(employee));

        assertEquals("Salary must be a positive value", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_ShouldThrowExceptionForNullSalary() {
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        Employee employee = new Employee("John Doe", null, "Address 1", "john@example.com", "john.official@example.com");
        employee.setId(1L);

        ServiceException exception = assertThrows(ServiceException.class, () -> employeeServicesTest.createEmployee(employee));

        assertEquals("Salary must be a positive value", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_ShouldThrowDatabaseExceptionOnSaveFailure() {
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        Employee employee = new Employee("John Doe", 50000.0, "Address 1", "john@example.com", "john.official@example.com");
        employee.setId(1L);

        when(employeeRepository.existsByName("John Doe")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("Database error"));

        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> employeeServicesTest.createEmployee(employee));
        assertEquals("Database operation failed.", exception.getMessage());
    }


//handling the post
@Test
void addEmployee_ShouldReturnBadRequestWhenServiceThrowsException() {
    // Arrange: Create a test employee
    Employee employee = new Employee("John Doe", 50000.0, "Address 1", "john@example.com", "john.official@example.com");
    employee.setId(1L);
    String errorMessage = "Employee with name John Doe already exists.";
    when(employeeServices.createEmployee(any(Employee.class)))
            .thenThrow(new ServiceException(errorMessage));
    ResponseEntity<Object> response = employeeController.addEmployee(employee);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof ErrorResponse);
    ErrorResponse errorResponse = (ErrorResponse) response.getBody();
    assertEquals(errorMessage, errorResponse.getMessage());
    verify(employeeServices, times(1)).createEmployee(any(Employee.class));
}

    @Test
    void existingEmployee_ShouldReturnEmployeeWhenExists() {
        // Arrange
        Long employeeId = 1L;
        Employee mockEmployee = new Employee("John Doe", 50000.0, "Address 1", "john@example.com", "john.official@example.com");
        mockEmployee.setId(employeeId);
    
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(mockEmployee));
    
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
    
        // Act
        Employee result = employeeServicesTest.existingEmployee(employeeId);
    
        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getId());
        assertEquals("John Doe", result.getName());
        
        // Verify repository was called
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void existingEmployee_ShouldThrowExceptionWhenNotFound() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
        
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        
        ServiceException exception = assertThrows(ServiceException.class, 
            () -> employeeServicesTest.existingEmployee(employeeId));
        
        assertEquals(String.format("Employee with ID %d not found", employeeId), exception.getMessage());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void deleteEmployee_ShouldThrowExceptionIfEmployeeNotFound() {
        Long id = 1L;
        doThrow(new ServiceException("Employee not found"))
                .when(employeeServices).deleteEmployee(id);
        ResponseEntity<Void> response = employeeController.deleteEmployee(id);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() {
        // Arrange: Existing employee in DB
        Employee existingEmployee = new Employee("John Doe", 50000.0, "123 Old St", "john@example.com", "john.official@example.com");
        existingEmployee.setId(1L);
    
        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Updated Name");
        updatedEmployee.setSalary(60000.0);
        updatedEmployee.setAddress("456 New St");
    
        when(employeeServices.updateEmployee(eq(1L), any(Employee.class))).thenReturn(updatedEmployee);
    
        // Act: Call controller method
        ResponseEntity<Employee> response = employeeController.updateEmployee(1L, updatedEmployee);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals(60000.0, response.getBody().getSalary());
        assertEquals("456 New St", response.getBody().getAddress());
    
        // Verify service method was called
        verify(employeeServices, times(1)).updateEmployee(eq(1L), any(Employee.class));
    }
    
    @Test
void updateEmployee_ShouldUpdateFieldsSuccessfully() {
    // Arrange: Create existing employee in DB
    Employee existingEmployee = new Employee("John Doe", 50000.0, "123 Old St", "john@example.com", "john.official@example.com");
    existingEmployee.setId(1L);

    Employee updatedEmployee = new Employee();
    updatedEmployee.setName("Updated Name");
    updatedEmployee.setSalary(60000.0);
    updatedEmployee.setAddress("456 New St");
    updatedEmployee.setPersonalMail("updated.personal@example.com");
    updatedEmployee.setOfficialMail("updated.official@example.com");

    when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));
    when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Use real service instance
    EmployeeServices realEmployeeService = new EmployeeServices(employeeRepository);

    // Act: Call actual service method
    Employee result = realEmployeeService.updateEmployee(1L, updatedEmployee);

    // Assert: Verify all fields were updated correctly
    assertNotNull(result);
    assertEquals("Updated Name", result.getName());
    assertEquals(60000.0, result.getSalary());
    assertEquals("456 New St", result.getAddress());
    assertEquals("updated.personal@example.com", result.getPersonalMail());
    assertEquals("updated.official@example.com", result.getOfficialMail());

    // Verify repository interactions
    verify(employeeRepository, times(1)).findById(1L);
    verify(employeeRepository, times(1)).save(any(Employee.class));
}

    @Test
    void updateEmployee_ShouldThrowExceptionIfEmployeeNotFound() {
        Long id = 1L;
        Employee employee = new Employee();
        when(employeeServices.updateEmployee(eq(id), any(Employee.class)))
                .thenThrow(new ServiceException("Employee not found"));

        ResponseEntity<Employee> response = employeeController.updateEmployee(id, employee);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void validateEmail_ShouldNotThrowExceptionForValidEmail() {
        assertDoesNotThrow(() -> employeeServices.validateEmail("test@example.com", "Personal"));
    }

    @Test
    void deleteEmployee_ShouldThrowExceptionIfNotFound() {
        EmployeeServices employeeServicesTest = new EmployeeServices(employeeRepository);
        Long employeeId = 1L;
        when(employeeRepository.existsById(employeeId)).thenReturn(false);
        ServiceException exception = assertThrows(ServiceException.class,
                () -> employeeServicesTest.deleteEmployee(employeeId));

        assertEquals("Employee with ID 1 not found", exception.getMessage());
        verify(employeeRepository, never()).deleteById(employeeId);
    }
    @Test
    void deleteEmployee_ShouldDeleteIfExists() {
        Long employeeId = 1L;
    
        when(employeeRepository.existsById(employeeId)).thenReturn(true);
    
        EmployeeServices realEmployeeService = new EmployeeServices(employeeRepository);
        realEmployeeService.deleteEmployee(employeeId);
    
        verify(employeeRepository, times(1)).deleteById(employeeId);
    }
    @Test
    void emloyeedelete_return_handling() {
        Long id = 1L;
        // Mock successful deletion (employee exists)
        doNothing().when(employeeServices).deleteEmployee(id);

        ResponseEntity<Void> response = employeeController.deleteEmployee(id);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(employeeServices, times(1)).deleteEmployee(id);
    }

    @Test
    void emloyeedelete_notFound_handling() {
        Long id = 1L;
        // Mock employee not found scenario
        doThrow(new ServiceException("Employee not found"))
            .when(employeeServices).deleteEmployee(id);

        ResponseEntity<Void> response = employeeController.deleteEmployee(id);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(employeeServices, times(1)).deleteEmployee(id);
    }
}
