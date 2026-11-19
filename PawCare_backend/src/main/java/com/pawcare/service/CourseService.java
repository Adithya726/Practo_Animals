package com.pawcare.service;

import java.util.List;

import com.pawcare.dto.CourseResponse;

public interface CourseService {

    List<CourseResponse> listAll();

    CourseResponse getBySlug(String slug);
}
