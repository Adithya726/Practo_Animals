package com.pawcare.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.pawcare.dto.LeadRequest;
import com.pawcare.dto.LeadResponse;
import com.pawcare.exception.BadRequestException;
import com.pawcare.exception.ResourceNotFoundException;
import com.pawcare.mapper.DtoMapper;
import com.pawcare.model.Course;
import com.pawcare.model.Lead;
import com.pawcare.model.LeadStatus;
import com.pawcare.model.LeadType;
import com.pawcare.repository.CourseRepository;
import com.pawcare.repository.LeadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final CourseRepository courseRepository;

    /**
     * Captures an enquiry or a seat registration. A registration needs a course
     * with seats left. Locking the course row makes two people racing for the
     * last seat queue up, so a course can never be overbooked. READ_COMMITTED
     * makes the seat count after the lock see registrations committed while
     * we waited (MySQL's default REPEATABLE READ could read a stale count).
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LeadResponse capture(LeadRequest request) {
        Course course = null;

        if (request.getCourseId() != null) {
            course = courseRepository.findByIdForUpdate(request.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        } else if (request.getType() == LeadType.REGISTRATION) {
            throw new BadRequestException("Choose a programme to register for");
        }

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (course != null && request.getType() == LeadType.REGISTRATION) {
            if (leadRepository.existsByEmailAndCourseIdAndType(email, course.getId(), LeadType.REGISTRATION)) {
                throw new BadRequestException("You have already registered for this programme with this email");
            }
            if (leadRepository.countActiveRegistrations(course.getId()) >= course.getSeatsTotal()) {
                throw new BadRequestException("This cohort is full. Send an enquiry and we will hold a seat in the next one");
            }
        }

        Lead lead = new Lead();
        lead.setName(request.getName().trim());
        lead.setEmail(email);
        lead.setPhone(request.getPhone().trim());
        lead.setBackground(request.getBackground());
        lead.setCourse(course);
        lead.setType(request.getType());
        lead.setMessage(TextUtil.blankToNull(request.getMessage()));
        String source = TextUtil.blankToNull(request.getSource());
        lead.setSource(source == null ? "direct" : source.toLowerCase(Locale.ROOT));

        return DtoMapper.toResponse(leadRepository.save(lead));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadResponse> listAll() {
        return leadRepository.findAllByOrderByCreatedAtDesc().stream().map(DtoMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public LeadResponse updateStatus(Long id, String status) {
        LeadStatus next = TextUtil.parseEnum(LeadStatus.class, status, "Status must be NEW, CONTACTED, ENROLLED or LOST");
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        lead.setStatus(next);
        return DtoMapper.toResponse(lead);
    }
}
