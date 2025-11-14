package com.example.hrmapplication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.example.hrmapplication.entity.Education;
import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.repository.EducationRepository;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.service.EducationService;
import com.example.hrmapplication.service.EmployeeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EducationControllerDiffblueTest {
    /**
     * Test {@link EducationController#delete(Long)}.
     *
     * <ul>
     *   <li>Given {@link EducationRepository} {@link EducationRepository#deleteById(Object)} does
     *       nothing.
     *   <li>Then calls {@link EducationRepository#deleteById(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link EducationController#delete(Long)}
     */
    @Test
    @DisplayName(
            "Test delete(Long); given EducationRepository deleteById(Object) does nothing; then calls deleteById(Object)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String EducationController.delete(Long)"})
    void testDelete_givenEducationRepositoryDeleteByIdDoesNothing_thenCallsDeleteById() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        Employee employee = new Employee();
        employee.preUpdate();
        employee.setAddress("42 Main St");
        employee.setAttendances(new ArrayList<>());
        employee.setBirthPlace("Birth Place");
        employee.setCitizenId("42");
        employee.setContracts(new ArrayList<>());
        employee.setCreatedAt(LocalDate.of(1970, 1, 1));
        employee.setCurrentAddress("42 Main St");
        employee.setDateOfBirth(LocalDate.of(1970, 1, 1));
        employee.setEducations(new ArrayList<>());
        employee.setEmail("jane.doe@example.org");
        employee.setFullName("Dr Jane Doe");
        employee.setGender("Gender");
        employee.setId(1L);
        employee.setInsurances(new ArrayList<>());
        employee.setPhone("6625550144");
        employee.setSalaries(new ArrayList<>());
        employee.setStatus("Status");
        employee.setUpdatedAt(LocalDate.of(1970, 1, 1));
        employee.setWorkHistories(new ArrayList<>());

        Education education = new Education();
        education.setCulturalLevel("Cultural Level");
        education.setDegree("Degree");
        education.setEmployee(employee);
        education.setFamilyBackground("Family Background");
        education.setForeignLanguage("en");
        education.setGraduationYear(1);
        education.setId(1L);
        education.setLanguageLevel("en");
        education.setSpecialization("Specialization");
        education.setUniversity("University");
        Optional<Education> ofResult = Optional.of(education);

        EducationRepository educationRepository = mock(EducationRepository.class);
        doNothing().when(educationRepository).deleteById(Mockito.<Long>any());
        when(educationRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        EducationService educationService = new EducationService(educationRepository);
        EmployeeService employeeService = new EmployeeService(mock(EmployeeRepository.class));

        EducationController educationController =
                new EducationController(educationService, employeeService);

        // Act
        String actualDeleteResult = educationController.delete(1L);

        // Assert
        verify(educationRepository).deleteById(1L);
        verify(educationRepository).findById(1L);
        assertEquals("redirect:/educations/employee/1", actualDeleteResult);
    }
}
