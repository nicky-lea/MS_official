package org.edu.usco.pw.ms_official.model;

import jakarta.persistence.*;

import javax.management.relation.Role;
import java.security.Timestamp;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cc; // asumiendo que 'cc' es tu clave primaria

    private String name;
    private String password;
    private String phone;
    private String address;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "registration_date")
    private Timestamp registrationDate;

}
