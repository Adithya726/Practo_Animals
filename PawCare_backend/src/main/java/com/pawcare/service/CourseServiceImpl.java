package com.pawcare.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawcare.dto.CourseResponse;
import com.pawcare.exception.ResourceNotFoundException;
import com.pawcare.mapper.DtoMapper;
import com.pawcare.model.Course;
import com.pawcare.repository.CourseRepository;
import com.pawcare.repository.LeadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final LeadRepository leadRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> listAll() {
        return courseRepository.findAllByOrderByStartDateAsc().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getBySlug(String slug) {
        return courseRepository.findBySlug(slug)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private CourseResponse toResponse(Course course) {
        return DtoMapper.toResponse(course, leadRepository.countActiveRegistrations(course.getId()));
    }
}
