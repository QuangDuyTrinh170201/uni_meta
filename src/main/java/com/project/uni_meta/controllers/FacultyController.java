package com.project.uni_meta.controllers;

import com.project.uni_meta.models.Faculty;
import com.project.uni_meta.models.Role;
import com.project.uni_meta.services.IFacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/faculties")
public class FacultyController {
    private final IFacultyService facultyService;
    @GetMapping("")
    public ResponseEntity<?> getAllFaculties(){
        List<Faculty> faculties = facultyService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }
}
