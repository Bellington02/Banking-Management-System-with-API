package com.bankingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**

 - Role Entity - Represents user roles in the system
 - This maps to the ‘roles’ table in the database
 -
 - Roles in our system:
 - - ROLE_ADMIN: Full system access (CEO/Administrator)
 - - ROLE_EMPLOYEE: Employee/Teller access
 - - ROLE_CUSTOMER: Customer access only
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     - This method runs automatically before saving a new role to the database
     - It sets the createdAt timestamp
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
