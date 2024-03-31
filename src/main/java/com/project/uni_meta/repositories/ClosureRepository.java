package com.project.uni_meta.repositories;

import com.project.uni_meta.models.Closure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClosureRepository extends JpaRepository<Closure, Long> {
    List<Closure> findByAcademicYearId(Long academicYearId);
}
