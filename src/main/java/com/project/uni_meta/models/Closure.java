package com.project.uni_meta.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "closures")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Closure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "final_deadline", nullable = false)
    private LocalDateTime finalDeadline;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private com.project.uni_meta.models.Faculty faculty;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private com.project.uni_meta.models.AcademicYear academicYear;
}
