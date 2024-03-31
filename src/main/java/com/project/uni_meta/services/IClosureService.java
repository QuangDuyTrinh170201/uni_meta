package com.project.uni_meta.services;

import com.project.uni_meta.dtos.ClosureDTO;
import com.project.uni_meta.models.Closure;

import java.util.List;

public interface IClosureService {
    List<Closure> getAllClosures();

    Closure addClosure(ClosureDTO closureDTO);

    Closure updateClosure(Long id, ClosureDTO closureDTO);

    void deleteClosure(Long id);

    public List<Closure> getClosureByAcademicId(Long id);
}
