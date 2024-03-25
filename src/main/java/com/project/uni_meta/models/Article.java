package com.project.uni_meta.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Article extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "filename", nullable = false)
    private String fileName;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "view", nullable = false)
    private Long view;

    @ManyToOne
    @JoinColumn(name = "academic_id")
    private com.project.uni_meta.models.AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private com.project.uni_meta.models.User user;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private com.project.uni_meta.models.Faculty faculty;
}
