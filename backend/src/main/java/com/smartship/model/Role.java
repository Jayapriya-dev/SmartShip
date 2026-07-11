package com.smartship.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Role entity - defines user roles: ADMIN, CUSTOMER, SHIPPER
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    public enum RoleName {
        ADMIN, CUSTOMER, SHIPPER
    }

    public Role(RoleName name) {
        this.name = name;
    }
}
