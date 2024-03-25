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

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Closure> addClosure(@RequestBody ClosureDTO closureDTO) {
        Closure newClosure = closureService.addClosure(closureDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newClosure);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Closure> updateClosure(@PathVariable Long id, @RequestBody ClosureDTO closureDTO) {
        Closure updatedClosure = closureService.updateClosure(id, closureDTO);
        return ResponseEntity.ok(updatedClosure);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteClosure(@PathVariable Long id) {
        closureService.deleteClosure(id);
        return ResponseEntity.noContent().build();
    }
}
