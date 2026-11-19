package com.pawcare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pawcare.dto.CourseResponse;
import com.pawcare.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> listAll() {
        return ResponseEntity.ok(courseService.listAll());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CourseResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(courseService.getBySlug(slug));
    }
}
