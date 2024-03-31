package com.project.uni_meta.controllers;

import com.project.uni_meta.dtos.ClosureDTO;
import com.project.uni_meta.models.Closure;
import com.project.uni_meta.services.IClosureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/closures")
@RequiredArgsConstructor
public class ClosureController {

    private final IClosureService closureService;

    @GetMapping
    public ResponseEntity<List<Closure>> getAllClosures() {
        List<Closure> closures = closureService.getAllClosures();
        return ResponseEntity.ok(closures);
    }

    @GetMapping("/academic/{academicId}")
    public ResponseEntity<?> getClosureByAcademicYear(@PathVariable Long academicId) {
        try{
            List<Closure> closures = closureService.getClosureByAcademicId(academicId);
            return ResponseEntity.ok(closures);
        }catch (Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addClosure(@RequestBody ClosureDTO closureDTO) {
        try{
            Closure newClosure = closureService.addClosure(closureDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newClosure);
        }catch (Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateClosure(@PathVariable Long id, @RequestBody ClosureDTO closureDTO) {
        try{
            Closure updatedClosure = closureService.updateClosure(id, closureDTO);
            return ResponseEntity.ok(updatedClosure);
        }catch (Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteClosure(@PathVariable Long id) {
        closureService.deleteClosure(id);
        return ResponseEntity.noContent().build();
    }
}
