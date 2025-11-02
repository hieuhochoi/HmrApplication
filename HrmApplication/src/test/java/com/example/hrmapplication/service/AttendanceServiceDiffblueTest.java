package com.example.hrmapplication.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.example.hrmapplication.entity.Attendance;
import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.repository.AttendanceRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AttendanceService.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class AttendanceServiceDiffblueTest {
    @MockBean
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AttendanceService attendanceService;

    /**
     * Test {@link AttendanceService#findByEmployeeId(Long)}.
     *
     * <p>Method under test: {@link AttendanceService#findByEmployeeId(Long)}
     */
    @Test
    @DisplayName("Test findByEmployeeId(Long)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List AttendanceService.findByEmployeeId(Long)"})
    void testFindByEmployeeId() {
        // Arrange
        when(attendanceRepository.findByEmployeeId(Mockito.<Long>any())).thenReturn(new ArrayList<>());

        // Act
        List<Attendance> actualFindByEmployeeIdResult = attendanceService.findByEmployeeId(1L);

        // Assert
        verify(attendanceRepository).findByEmployeeId(1L);
        assertTrue(actualFindByEmployeeIdResult.isEmpty());
    }

    /**
     * Test {@link AttendanceService#findByDateRange(Long, LocalDate, LocalDate)}.
     *
     * <p>Method under test: {@link AttendanceService#findByDateRange(Long, LocalDate, LocalDate)}
     */
    @Test
    @DisplayName("Test findByDateRange(Long, LocalDate, LocalDate)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List AttendanceService.findByDateRange(Long, LocalDate, LocalDate)"})
    void testFindByDateRange() {
        // Arrange
        when(attendanceRepository.findByEmployeeIdAndWorkDateBetween(
                Mockito.<Long>any(), Mockito.<LocalDate>any(), Mockito.<LocalDate>any()))
                .thenReturn(new ArrayList<>());

        // Act
        List<Attendance> actualFindByDateRangeResult =
                attendanceService.findByDateRange(1L, LocalDate.of(1970, 1, 1), LocalDate.of(1970, 1, 1));

        // Assert
        verify(attendanceRepository)
                .findByEmployeeIdAndWorkDateBetween(eq(1L), isA(LocalDate.class), isA(LocalDate.class));
        assertTrue(actualFindByDateRangeResult.isEmpty());
    }

    /**
     * Test {@link AttendanceService#save(Attendance)}.
     *
     * <p>Method under test: {@link AttendanceService#save(Attendance)}
     */
    @Test
    @DisplayName("Test save(Attendance)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Attendance AttendanceService.save(Attendance)"})
    void testSave() {
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

        Attendance attendance = new Attendance();
        attendance.setCheckIn(LocalTime.MIDNIGHT);
        attendance.setCheckOut(LocalTime.MIDNIGHT);
        attendance.setEmployee(employee);
        attendance.setId(1L);
        attendance.setIsLeave(true);
        attendance.setLeaveReason("Just cause");
        attendance.setOvertimeHours(10.0d);
        attendance.setShift("Shift");
        attendance.setStatus("Status");
        attendance.setWorkDate(LocalDate.of(1970, 1, 1));
        attendance.setWorkHours(10.0d);
        when(attendanceRepository.save(Mockito.<Attendance>any())).thenReturn(attendance);

        Employee employee2 = new Employee();
        employee2.preUpdate();
        employee2.setAddress("42 Main St");
        employee2.setAttendances(new ArrayList<>());
        employee2.setBirthPlace("Birth Place");
        employee2.setCitizenId("42");
        employee2.setContracts(new ArrayList<>());
        employee2.setCreatedAt(LocalDate.of(1970, 1, 1));
        employee2.setCurrentAddress("42 Main St");
        employee2.setDateOfBirth(LocalDate.of(1970, 1, 1));
        employee2.setEducations(new ArrayList<>());
        employee2.setEmail("jane.doe@example.org");
        employee2.setFullName("Dr Jane Doe");
        employee2.setGender("Gender");
        employee2.setId(1L);
        employee2.setInsurances(new ArrayList<>());
        employee2.setPhone("6625550144");
        employee2.setSalaries(new ArrayList<>());
        employee2.setStatus("Status");
        employee2.setUpdatedAt(LocalDate.of(1970, 1, 1));
        employee2.setWorkHistories(new ArrayList<>());

        Attendance attendance2 = new Attendance();
        attendance2.setCheckIn(LocalTime.MIDNIGHT);
        attendance2.setCheckOut(LocalTime.MIDNIGHT);
        attendance2.setEmployee(employee2);
        attendance2.setId(1L);
        attendance2.setIsLeave(true);
        attendance2.setLeaveReason("Just cause");
        attendance2.setOvertimeHours(10.0d);
        attendance2.setShift("Shift");
        attendance2.setStatus("Status");
        attendance2.setWorkDate(LocalDate.of(1970, 1, 1));
        attendance2.setWorkHours(10.0d);

        // Act
        Attendance actualSaveResult = attendanceService.save(attendance2);

        // Assert
        verify(attendanceRepository).save(isA(Attendance.class));
        assertSame(attendance, actualSaveResult);
    }

    /**
     * Test {@link AttendanceService#delete(Long)}.
     *
     * <p>Method under test: {@link AttendanceService#delete(Long)}
     */
    @Test
    @DisplayName("Test delete(Long)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void AttendanceService.delete(Long)"})
    void testDelete() {
        // Arrange
        doNothing().when(attendanceRepository).deleteById(Mockito.<Long>any());

        // Act
        attendanceService.delete(1L);

        // Assert
        verify(attendanceRepository).deleteById(1L);
    }
}
