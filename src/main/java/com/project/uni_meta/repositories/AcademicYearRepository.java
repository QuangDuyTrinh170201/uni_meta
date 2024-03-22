package com.project.uni_meta.repositories;

import com.project.uni_meta.models.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    boolean existsByYear(String year);
}
