package com.example.employee_mgmt.modal;  

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "employee") 
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private Double salary;
 
    @Column(nullable = true)
    private String address;

    @Column(name = "personal_mail", nullable = true)
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Invalid email format")
    private String personalMail;

    @Column(name = "official_mail", nullable = true)
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Invalid email format")
    private String officialMail;




    // Constructors
    public Employee() {}

    public Employee(String name, Double salary, String address, String personalMail, String officialMail) {
        this.name = name;
        this.salary = salary;
        this.address = address;
        this.personalMail = personalMail;
        this.officialMail = officialMail;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPersonalMail() { return personalMail; }
    public void setPersonalMail(String personalMail) { this.personalMail = personalMail; }

    public String getOfficialMail() { return officialMail; }
    public void setOfficialMail(String officialMail) { this.officialMail = officialMail; }
}
