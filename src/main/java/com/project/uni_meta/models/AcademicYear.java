package com.project.uni_meta.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "academic_years")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcademicYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year", nullable = false)
    private String year;

    @Column(name = "current", nullable = false)
    private Long current;
}
