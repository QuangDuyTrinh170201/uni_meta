package com.project.uni_meta.services;

import com.project.uni_meta.dtos.AcademicYearDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.models.AcademicYear;
import com.project.uni_meta.repositories.AcademicYearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AcademicYearService implements IAcademicYear{
    private final AcademicYearRepository academicYearRepository;
    @Override
    public List<AcademicYear> getAllYears(){
        return academicYearRepository.findAll();
    }

    @Override
    public AcademicYear addNewAcademicYear(AcademicYearDTO academicYearDTO) throws Exception {
        String year = academicYearDTO.getYear();
        if(academicYearRepository.existsByYear(year)){
            throw new DataIntegrityViolationException("This academic year has been existed");
        }
        AcademicYear academicYear = AcademicYear
                .builder()
                .year(academicYearDTO.getYear())
                .current(academicYearDTO.getCurrent())
                .build();
        return academicYearRepository.save(academicYear);
    }

    @Override
    public AcademicYear updateYear(Long id, AcademicYearDTO academicYearDTO) throws Exception {
        AcademicYear existingAcademicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find this academic year"));
        // Get all academic years
        Optional<AcademicYear> checkCurrent = academicYearRepository.findCurrentYear();

        checkCurrent.ifPresent(academicYear -> academicYear.setCurrent(0L));

        String newAcademicYear = academicYearDTO.getYear();
        if(newAcademicYear!=null){
            existingAcademicYear.setYear(newAcademicYear);
        }

        Long newCurrent = academicYearDTO.getCurrent();
        if(newCurrent != null){
            existingAcademicYear.setCurrent(newCurrent);
        }
        return academicYearRepository.save(existingAcademicYear);
    }

    @Override
    public void deleteYear(Long id) throws Exception {
        if(academicYearRepository.findById(id).isEmpty()){
            throw new DataNotFoundException("Cannot find this academic year!");
        }
        academicYearRepository.deleteById(id);
    }
}
