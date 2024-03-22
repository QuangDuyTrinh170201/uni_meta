package com.project.uni_meta.services;

import com.project.uni_meta.dtos.AcademicYearDTO;
import com.project.uni_meta.models.AcademicYear;

import java.util.List;

public interface IAcademicYear {
    List<AcademicYear> getAllYears();
    AcademicYear addNewAcademicYear(AcademicYearDTO academicYearDTO) throws Exception;
    AcademicYear updateYear(Long id, AcademicYearDTO academicYearDTO) throws Exception;
    void deleteYear(Long id) throws Exception;
}
