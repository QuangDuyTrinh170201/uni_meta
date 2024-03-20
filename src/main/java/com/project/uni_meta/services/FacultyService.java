package com.project.uni_meta.services;

import com.project.uni_meta.models.Faculty;
import com.project.uni_meta.repositories.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultyService implements IFacultyService{
    private final FacultyRepository facultyRepository;
    @Override
    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }
}
