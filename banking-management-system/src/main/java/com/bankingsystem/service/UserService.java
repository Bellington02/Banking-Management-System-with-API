package com.bankingsystem.service;

import com.bankingsystem.dto.request.CustomerRegistrationRequest;
import com.bankingsystem.dto.request.EmployeeRegistrationRequest;
import com.bankingsystem.entity.*;
import com.bankingsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerCustomer(CustomerRegistrationRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setIsActive(true);

        // Assign CUSTOMER role
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Customer role not found"));
        user.addRole(customerRole);

        // Save user
        user = userRepository.save(user);

        // Create Customer
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setCustomerNumber(generateCustomerNumber());
        customer.setIdNumber(request.getIdNumber());
        customer.setOccupation(request.getOccupation());
        customer.setAnnualIncome(request.getAnnualIncome());
        customer.setAccountStatus("ACTIVE");
        customer.setKycVerified(false);

        customerRepository.save(customer);

        return user;
    }

    @Transactional
    public User registerEmployee(EmployeeRegistrationRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setIsActive(true);

        // Assign EMPLOYEE role
        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Employee role not found"));
        user.addRole(employeeRole);

        // Save user
        user = userRepository.save(user);

        // Create Employee
        Employee employee = new Employee();
        employee.setUser(user);
        employee.setEmployeeNumber(generateEmployeeNumber());
        employee.setDepartment(request.getDepartment());
        employee.setPosition(request.getPosition());
        employee.setHireDate(request.getHireDate());
        employee.setSalary(request.getSalary());
        employee.setEmploymentStatus("ACTIVE");

        employeeRepository.save(employee);

        return user;
    }

    private String generateCustomerNumber() {
        return "CUST-" + System.currentTimeMillis();
    }

    private String generateEmployeeNumber() {
        return "EMP-" + System.currentTimeMillis();
    }
}