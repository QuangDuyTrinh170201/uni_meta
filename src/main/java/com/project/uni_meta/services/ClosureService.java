package com.project.uni_meta.services;

import com.project.uni_meta.dtos.ClosureDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.AcademicYear;
import com.project.uni_meta.models.Closure;
import com.project.uni_meta.models.Faculty;
import com.project.uni_meta.repositories.AcademicYearRepository;
import com.project.uni_meta.repositories.ClosureRepository;
import com.project.uni_meta.repositories.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClosureService implements IClosureService{
    private final ClosureRepository closureRepository;
    private final FacultyRepository facultyRepository;
    private final AcademicYearRepository academicYearRepository;

    @Override
    public List<Closure> getAllClosures() {
        return closureRepository.findAll();
    }

    @Override
    public Closure addClosure(ClosureDTO closureDTO) {
        Faculty faculty = null;
        try {
            faculty = facultyRepository.findById(closureDTO.getFacultyId())
                    .orElseThrow(() -> new DataNotFoundException("Faculty not found with id: " + closureDTO.getFacultyId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        AcademicYear academicYear = null;
        try {
            academicYear = academicYearRepository.findById(closureDTO.getAcademicYearId())
                    .orElseThrow(() -> new DataNotFoundException("Academic year not found with id: " + closureDTO.getAcademicYearId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Closure> checkDuplicate = closureRepository.findAll();

        for(Closure closures : checkDuplicate){
            if(Objects.equals(closures.getFaculty().getId(), closureDTO.getFacultyId()) && Objects.equals(closures.getAcademicYear().getId(), closureDTO.getAcademicYearId())){
                throw new DataIntegrityViolationException("Already has this closure date in faculty on year, you can change that closure date!");
            }
        }

        Closure closure = Closure.builder()
                .deadline(closureDTO.getDeadline())
                .finalDeadline(closureDTO.getFinalDeadline())
                .faculty(faculty)
                .academicYear(academicYear)
                .build();

        return closureRepository.save(closure);
    }

    @Override
    public Closure updateClosure(Long id, ClosureDTO closureDTO) {
        Closure existingClosure = null;
        try {
            existingClosure = closureRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Closure not found with id: " + id));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        Faculty faculty = null;
        try {
            faculty = facultyRepository.findById(closureDTO.getFacultyId())
                    .orElseThrow(() -> new DataNotFoundException("Faculty not found with id: " + closureDTO.getFacultyId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        AcademicYear academicYear = null;
        try {
            academicYear = academicYearRepository.findById(closureDTO.getAcademicYearId())
                    .orElseThrow(() -> new DataNotFoundException("Academic year not found with id: " + closureDTO.getAcademicYearId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Closure> checkDuplicate = closureRepository.findAll();

        for(Closure closures : checkDuplicate){
            if(Objects.equals(closures.getFaculty().getId(), closureDTO.getFacultyId()) && Objects.equals(closures.getAcademicYear().getId(), closureDTO.getAcademicYearId())
                    && !Objects.equals(closures.getId(), id)){
                throw new DataIntegrityViolationException("Already has this closure date in faculty on year, you can change that closure date!");
            }
        }

        existingClosure.setDeadline(closureDTO.getDeadline());
        existingClosure.setFinalDeadline(closureDTO.getFinalDeadline());
        existingClosure.setFaculty(faculty);
        existingClosure.setAcademicYear(academicYear);

        return closureRepository.save(existingClosure);
    }

    @Override
    public void deleteClosure(Long id) {
        if (!closureRepository.existsById(id)) {
            try {
                throw new DataNotFoundException("Closure not found with id: " + id);
            } catch (DataNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        closureRepository.deleteById(id);
    }

    @Override
    public List<Closure> getClosureByAcademicId(Long id){
        List<Closure> findByAcademic = closureRepository.findByAcademicYearId(id);
        if(findByAcademic.isEmpty()){
            throw new DataIntegrityViolationException("Cannot find any closures with this academic year!");
        }
        return findByAcademic;
    }
}
