package com.example.hrmapplication.mapper;

import com.example.hrmapplication.dto.EducationRequest;
import com.example.hrmapplication.dto.EducationResponse;
import com.example.hrmapplication.entity.Education;
import org.springframework.stereotype.Component;

@Component
public class EducationMapper {

    public Education toEntity(EducationRequest request) {
        if (request == null) {
            return null;
        }
        Education education = new Education();
        education.setId(request.getId());
        applyCommonFields(education, request);
        return education;
    }

    public EducationResponse toResponse(Education education) {
        if (education == null) {
            return null;
        }
        return EducationResponse.builder()
                .id(education.getId())
                .employeeId(education.getEmployee() != null ? education.getEmployee().getId() : null)
                .employeeName(education.getEmployee() != null ? education.getEmployee().getFullName() : null)
                .familyBackground(education.getFamilyBackground())
                .culturalLevel(education.getCulturalLevel())
                .specialization(education.getSpecialization())
                .foreignLanguage(education.getForeignLanguage())
                .languageLevel(education.getLanguageLevel())
                .degree(education.getDegree())
                .university(education.getUniversity())
                .graduationYear(education.getGraduationYear())
                .build();
    }

    public EducationRequest toRequest(Education education) {
        if (education == null) {
            return null;
        }
        EducationRequest request = new EducationRequest();
        request.setId(education.getId());
        request.setEmployeeId(education.getEmployee() != null ? education.getEmployee().getId() : null);
        request.setFamilyBackground(education.getFamilyBackground());
        request.setCulturalLevel(education.getCulturalLevel());
        request.setSpecialization(education.getSpecialization());
        request.setForeignLanguage(education.getForeignLanguage());
        request.setLanguageLevel(education.getLanguageLevel());
        request.setDegree(education.getDegree());
        request.setUniversity(education.getUniversity());
        request.setGraduationYear(education.getGraduationYear());
        return request;
    }

    public void updateEntity(Education entity, EducationRequest request) {
        if (entity == null || request == null) {
            return;
        }
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(Education target, EducationRequest request) {
        target.setFamilyBackground(request.getFamilyBackground());
        target.setCulturalLevel(request.getCulturalLevel());
        target.setSpecialization(request.getSpecialization());
        target.setForeignLanguage(request.getForeignLanguage());
        target.setLanguageLevel(request.getLanguageLevel());
        target.setDegree(request.getDegree());
        target.setUniversity(request.getUniversity());
        target.setGraduationYear(request.getGraduationYear());
    }
}

