package com.project.uni_meta.controllers;

import com.project.uni_meta.dtos.AcademicYearDTO;
import com.project.uni_meta.models.AcademicYear;
import com.project.uni_meta.services.AcademicYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/academic_years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @GetMapping
    public ResponseEntity<List<AcademicYear>> getAllAcademicYears() {
        List<AcademicYear> academicYears = academicYearService.getAllYears();
        return ResponseEntity.ok(academicYears);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AcademicYear> addAcademicYear(@RequestBody AcademicYearDTO academicYearDTO) {
        try {
            AcademicYear newAcademicYear = academicYearService.addNewAcademicYear(academicYearDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAcademicYear);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AcademicYear> updateAcademicYear(@PathVariable Long id, @RequestBody AcademicYearDTO academicYearDTO) {
        try {
            AcademicYear updatedAcademicYear = academicYearService.updateYear(id, academicYearDTO);
            return ResponseEntity.ok(updatedAcademicYear);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAcademicYear(@PathVariable Long id) {
        try {
            academicYearService.deleteYear(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
