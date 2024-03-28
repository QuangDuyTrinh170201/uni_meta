package com.project.uni_meta.repositories;

import com.project.uni_meta.models.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    boolean existsByYear(String year);

    @Query("SELECT ay FROM AcademicYear ay WHERE ay.current = 1")
    Optional<AcademicYear> findCurrentYear();
}
