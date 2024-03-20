package com.project.uni_meta.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public static String GUEST = "GUEST";
    public static String STUDENT = "STUDENT";
    public static String MARKETING_COORDINATOR = "MARKETING_COORDINATOR";
    public static String MARKETING_MANAGER = "MARKETING_MANAGER";
    public static String ADMIN = "ADMIN";
}