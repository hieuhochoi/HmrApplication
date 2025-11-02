package com.example.hrmapplication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.example.hrmapplication.entity.Contract;
import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.repository.ContractRepository;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.service.ContractService;
import com.example.hrmapplication.service.EmployeeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

class ContractControllerDiffblueTest {
    /**
     * Test {@link ContractController#listByEmployee(Long, Model)}.
     *
     * <p>Method under test: {@link ContractController#listByEmployee(Long, Model)}
     */
    @Test
    @DisplayName("Test listByEmployee(Long, Model)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.listByEmployee(Long, Model)"})
    void testListByEmployee() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        ContractService contractService = mock(ContractService.class);
        ArrayList<Contract> contractList = new ArrayList<>();
        when(contractService.findByEmployeeId(Mockito.<Long>any())).thenReturn(contractList);

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
        Optional<Employee> ofResult = Optional.of(employee);

        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        when(employeeRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        EmployeeService employeeService = new EmployeeService(employeeRepository);

        ContractController contractController =
                new ContractController(contractService, employeeService);
        ConcurrentModel model = new ConcurrentModel();

        // Act
        String actualListByEmployeeResult = contractController.listByEmployee(1L, model);

        // Assert
        verify(contractService).findByEmployeeId(1L);
        verify(employeeRepository).findById(1L);
        assertEquals(3, model.size());
        Object getResult = model.get("contracts");
        assertTrue(getResult instanceof List);
        assertEquals("Dr Jane Doe", model.get("employeeName"));
        assertEquals("contract/list", actualListByEmployeeResult);
        assertEquals(1L, ((Long) model.get("employeeId")).longValue());
        assertTrue(((List<Object>) getResult).isEmpty());
        assertSame(contractList, getResult);
    }

    /**
     * Test {@link ContractController#listByEmployee(Long, Model)}.
     *
     * <ul>
     *   <li>Given {@link EmployeeService} {@link EmployeeService#findById(Long)} return {@link
     *       Employee#Employee()}.
     *   <li>Then calls {@link EmployeeService#findById(Long)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#listByEmployee(Long, Model)}
     */
    @Test
    @DisplayName(
            "Test listByEmployee(Long, Model); given EmployeeService findById(Long) return Employee(); then calls findById(Long)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.listByEmployee(Long, Model)"})
    void testListByEmployee_givenEmployeeServiceFindByIdReturnEmployee_thenCallsFindById() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        ContractService contractService = mock(ContractService.class);
        ArrayList<Contract> contractList = new ArrayList<>();
        when(contractService.findByEmployeeId(Mockito.<Long>any())).thenReturn(contractList);

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

        EmployeeService employeeService = mock(EmployeeService.class);
        when(employeeService.findById(Mockito.<Long>any())).thenReturn(employee);

        ContractController contractController =
                new ContractController(contractService, employeeService);
        ConcurrentModel model = new ConcurrentModel();

        // Act
        String actualListByEmployeeResult = contractController.listByEmployee(1L, model);

        // Assert
        verify(contractService).findByEmployeeId(1L);
        verify(employeeService).findById(1L);
        assertEquals(3, model.size());
        Object getResult = model.get("contracts");
        assertTrue(getResult instanceof List);
        assertEquals("Dr Jane Doe", model.get("employeeName"));
        assertEquals("contract/list", actualListByEmployeeResult);
        assertEquals(1L, ((Long) model.get("employeeId")).longValue());
        assertTrue(((List<Object>) getResult).isEmpty());
        assertSame(contractList, getResult);
    }

    /**
     * Test {@link ContractController#listByEmployee(Long, Model)}.
     *
     * <ul>
     *   <li>Then calls {@link ContractRepository#findByEmployeeId(Long)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#listByEmployee(Long, Model)}
     */
    @Test
    @DisplayName("Test listByEmployee(Long, Model); then calls findByEmployeeId(Long)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.listByEmployee(Long, Model)"})
    void testListByEmployee_thenCallsFindByEmployeeId() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        ContractRepository contractRepository = mock(ContractRepository.class);
        ArrayList<Contract> contractList = new ArrayList<>();
        when(contractRepository.findByEmployeeId(Mockito.<Long>any())).thenReturn(contractList);
        ContractService contractService = new ContractService(contractRepository);

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
        Optional<Employee> ofResult = Optional.of(employee);

        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        when(employeeRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        EmployeeService employeeService = new EmployeeService(employeeRepository);

        ContractController contractController =
                new ContractController(contractService, employeeService);
        ConcurrentModel model = new ConcurrentModel();

        // Act
        String actualListByEmployeeResult = contractController.listByEmployee(1L, model);

        // Assert
        verify(contractRepository).findByEmployeeId(1L);
        verify(employeeRepository).findById(1L);
        assertEquals(3, model.size());
        Object getResult = model.get("contracts");
        assertTrue(getResult instanceof List);
        assertEquals("Dr Jane Doe", model.get("employeeName"));
        assertEquals("contract/list", actualListByEmployeeResult);
        assertEquals(1L, ((Long) model.get("employeeId")).longValue());
        assertTrue(((List<Object>) getResult).isEmpty());
        assertSame(contractList, getResult);
    }

    /**
     * Test {@link ContractController#showForm(Long, Model)}.
     *
     * <ul>
     *   <li>Given {@link EmployeeRepository} {@link EmployeeRepository#findById(Object)} return of
     *       {@link Employee#Employee()}.
     *   <li>Then calls {@link EmployeeRepository#findById(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#showForm(Long, Model)}
     */
    @Test
    @DisplayName(
            "Test showForm(Long, Model); given EmployeeRepository findById(Object) return of Employee(); then calls findById(Object)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.showForm(Long, Model)"})
    void testShowForm_givenEmployeeRepositoryFindByIdReturnOfEmployee_thenCallsFindById() {
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
        Optional<Employee> ofResult = Optional.of(employee);

        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        when(employeeRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        ContractService contractService = new ContractService(mock(ContractRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);
        ConcurrentModel model = new ConcurrentModel();

        // Act
        String actualShowFormResult = contractController.showForm(1L, model);

        // Assert
        verify(employeeRepository).findById(1L);
        assertEquals(1, model.size());
        Object getResult = model.get("contract");
        assertTrue(getResult instanceof Contract);
        assertEquals("ACTIVE", ((Contract) getResult).getStatus());
        assertEquals("contract/form", actualShowFormResult);
        assertNull(((Contract) getResult).getSalary());
        assertNull(((Contract) getResult).getDuration());
        assertNull(((Contract) getResult).getId());
        assertNull(((Contract) getResult).getContractNumber());
        assertNull(((Contract) getResult).getContractType());
        assertNull(((Contract) getResult).getEndDate());
        assertNull(((Contract) getResult).getSignedDate());
        assertNull(((Contract) getResult).getStartDate());
        assertSame(employee, ((Contract) getResult).getEmployee());
    }

    /**
     * Test {@link ContractController#showForm(Long, Model)}.
     *
     * <ul>
     *   <li>Given {@link EmployeeService} {@link EmployeeService#findById(Long)} return {@link
     *       Employee#Employee()}.
     *   <li>Then calls {@link EmployeeService#findById(Long)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#showForm(Long, Model)}
     */
    @Test
    @DisplayName(
            "Test showForm(Long, Model); given EmployeeService findById(Long) return Employee(); then calls findById(Long)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.showForm(Long, Model)"})
    void testShowForm_givenEmployeeServiceFindByIdReturnEmployee_thenCallsFindById() {
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

        EmployeeService employeeService = mock(EmployeeService.class);
        when(employeeService.findById(Mockito.<Long>any())).thenReturn(employee);
        ContractService contractService = new ContractService(mock(ContractRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);
        ConcurrentModel model = new ConcurrentModel();

        // Act
        String actualShowFormResult = contractController.showForm(1L, model);

        // Assert
        verify(employeeService).findById(1L);
        assertEquals(1, model.size());
        Object getResult = model.get("contract");
        assertTrue(getResult instanceof Contract);
        assertEquals("ACTIVE", ((Contract) getResult).getStatus());
        assertEquals("contract/form", actualShowFormResult);
        assertNull(((Contract) getResult).getSalary());
        assertNull(((Contract) getResult).getDuration());
        assertNull(((Contract) getResult).getId());
        assertNull(((Contract) getResult).getContractNumber());
        assertNull(((Contract) getResult).getContractType());
        assertNull(((Contract) getResult).getEndDate());
        assertNull(((Contract) getResult).getSignedDate());
        assertNull(((Contract) getResult).getStartDate());
        assertSame(employee, ((Contract) getResult).getEmployee());
    }

    /**
     * Test {@link ContractController#save(Contract)}.
     *
     * <ul>
     *   <li>Given {@link ContractRepository} {@link ContractRepository#save(Object)} return {@link
     *       Contract#Contract()}.
     *   <li>Then calls {@link ContractRepository#save(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#save(Contract)}
     */
    @Test
    @DisplayName(
            "Test save(Contract); given ContractRepository save(Object) return Contract(); then calls save(Object)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.save(Contract)"})
    void testSave_givenContractRepositorySaveReturnContract_thenCallsSave() {
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

        Contract contract = new Contract();
        contract.setContractNumber("42");
        contract.setContractType("Contract Type");
        contract.setDuration(1);
        contract.setEmployee(employee);
        contract.setEndDate(LocalDate.of(1970, 1, 1));
        contract.setId(1L);
        contract.setSalary(10.0d);
        contract.setSignedDate(LocalDate.of(1970, 1, 1));
        contract.setStartDate(LocalDate.of(1970, 1, 1));
        contract.setStatus("Status");

        ContractRepository contractRepository = mock(ContractRepository.class);
        when(contractRepository.save(Mockito.<Contract>any())).thenReturn(contract);
        ContractService contractService = new ContractService(contractRepository);
        EmployeeService employeeService = new EmployeeService(mock(EmployeeRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);

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

        Contract contract2 = new Contract();
        contract2.setContractNumber("42");
        contract2.setContractType("Contract Type");
        contract2.setDuration(1);
        contract2.setEmployee(employee2);
        contract2.setEndDate(LocalDate.of(1970, 1, 1));
        contract2.setId(1L);
        contract2.setSalary(10.0d);
        contract2.setSignedDate(LocalDate.of(1970, 1, 1));
        contract2.setStartDate(LocalDate.of(1970, 1, 1));
        contract2.setStatus("Status");

        // Act
        String actualSaveResult = contractController.save(contract2);

        // Assert
        verify(contractRepository).save(isA(Contract.class));
        assertEquals("redirect:/contracts/employee/1", actualSaveResult);
    }

    /**
     * Test {@link ContractController#save(Contract)}.
     *
     * <ul>
     *   <li>Given {@link ContractService} {@link ContractService#save(Contract)} return {@link
     *       Contract#Contract()}.
     *   <li>Then calls {@link ContractService#save(Contract)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#save(Contract)}
     */
    @Test
    @DisplayName(
            "Test save(Contract); given ContractService save(Contract) return Contract(); then calls save(Contract)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.save(Contract)"})
    void testSave_givenContractServiceSaveReturnContract_thenCallsSave() {
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

        Contract contract = new Contract();
        contract.setContractNumber("42");
        contract.setContractType("Contract Type");
        contract.setDuration(1);
        contract.setEmployee(employee);
        contract.setEndDate(LocalDate.of(1970, 1, 1));
        contract.setId(1L);
        contract.setSalary(10.0d);
        contract.setSignedDate(LocalDate.of(1970, 1, 1));
        contract.setStartDate(LocalDate.of(1970, 1, 1));
        contract.setStatus("Status");

        ContractService contractService = mock(ContractService.class);
        when(contractService.save(Mockito.<Contract>any())).thenReturn(contract);
        EmployeeService employeeService = new EmployeeService(mock(EmployeeRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);

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

        Contract contract2 = new Contract();
        contract2.setContractNumber("42");
        contract2.setContractType("Contract Type");
        contract2.setDuration(1);
        contract2.setEmployee(employee2);
        contract2.setEndDate(LocalDate.of(1970, 1, 1));
        contract2.setId(1L);
        contract2.setSalary(10.0d);
        contract2.setSignedDate(LocalDate.of(1970, 1, 1));
        contract2.setStartDate(LocalDate.of(1970, 1, 1));
        contract2.setStatus("Status");

        // Act
        String actualSaveResult = contractController.save(contract2);

        // Assert
        verify(contractService).save(isA(Contract.class));
        assertEquals("redirect:/contracts/employee/1", actualSaveResult);
    }

    /**
     * Test {@link ContractController#edit(Long, Model)}.
     *
     * <ul>
     *   <li>Given {@link ContractRepository} {@link ContractRepository#findById(Object)} return of
     *       {@link Contract#Contract()}.
     *   <li>Then calls {@link ContractRepository#findById(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#edit(Long, Model)}
     */
    @Test
    @DisplayName(
            "Test edit(Long, Model); given ContractRepository findById(Object) return of Contract(); then calls findById(Object)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.edit(Long, Model)"})
    void testEdit_givenContractRepositoryFindByIdReturnOfContract_thenCallsFindById() {
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

        Contract contract = new Contract();
        contract.setContractNumber("42");
        contract.setContractType("Contract Type");
        contract.setDuration(1);
        contract.setEmployee(employee);
        contract.setEndDate(LocalDate.of(1970, 1, 1));
        contract.setId(1L);
        contract.setSalary(10.0d);
        contract.setSignedDate(LocalDate.of(1970, 1, 1));
        contract.setStartDate(LocalDate.of(1970, 1, 1));
        contract.setStatus("Status");
        Optional<Contract> ofResult = Optional.of(contract);

        ContractRepository contractRepository = mock(ContractRepository.class);
        when(contractRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ContractService contractService = new ContractService(contractRepository);
        EmployeeService employeeService = new EmployeeService(mock(EmployeeRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);
        ConcurrentModel model = new ConcurrentModel();

        // Act
        String actualEditResult = contractController.edit(1L, model);

        // Assert
        verify(contractRepository).findById(1L);
        assertEquals("contract/form", actualEditResult);
        assertEquals(1, model.size());
        assertSame(contract, model.get("contract"));
    }

    /**
     * Test {@link ContractController#edit(Long, Model)}.
     *
     * <ul>
     *   <li>Given {@link ContractService} {@link ContractService#findById(Long)} return {@link
     *       Contract#Contract()}.
     *   <li>Then calls {@link ContractService#findById(Long)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#edit(Long, Model)}
     */
    @Test
    @DisplayName(
            "Test edit(Long, Model); given ContractService findById(Long) return Contract(); then calls findById(Long)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.edit(Long, Model)"})
    void testEdit_givenContractServiceFindByIdReturnContract_thenCallsFindById() {
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

        Contract contract = new Contract();
        contract.setContractNumber("42");
        contract.setContractType("Contract Type");
        contract.setDuration(1);
        contract.setEmployee(employee);
        contract.setEndDate(LocalDate.of(1970, 1, 1));
        contract.setId(1L);
        contract.setSalary(10.0d);
        contract.setSignedDate(LocalDate.of(1970, 1, 1));
        contract.setStartDate(LocalDate.of(1970, 1, 1));
        contract.setStatus("Status");

        ContractService contractService = mock(ContractService.class);
        when(contractService.findById(Mockito.<Long>any())).thenReturn(contract);
        EmployeeService employeeService = new EmployeeService(mock(EmployeeRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);
        ConcurrentModel model = new ConcurrentModel();

        // Act
        String actualEditResult = contractController.edit(1L, model);

        // Assert
        verify(contractService).findById(1L);
        assertEquals("contract/form", actualEditResult);
        assertEquals(1, model.size());
        assertSame(contract, model.get("contract"));
    }

    /**
     * Test {@link ContractController#detail(Long, Model)}.
     *
     * <ul>
     *   <li>Given {@link ContractRepository} {@link ContractRepository#findById(Object)} return of
     *       {@link Contract#Contract()}.
     *   <li>Then calls {@link ContractRepository#findById(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#detail(Long, Model)}
     */
    @Test
    @DisplayName(
            "Test detail(Long, Model); given ContractRepository findById(Object) return of Contract(); then calls findById(Object)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.detail(Long, Model)"})
    void testDetail_givenContractRepositoryFindByIdReturnOfContract_thenCallsFindById() {
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

        Contract contract = new Contract();
        contract.setContractNumber("42");
        contract.setContractType("Contract Type");
        contract.setDuration(1);
        contract.setEmployee(employee);
        contract.setEndDate(LocalDate.of(1970, 1, 1));
        contract.setId(1L);
        contract.setSalary(10.0d);
        contract.setSignedDate(LocalDate.of(1970, 1, 1));
        contract.setStartDate(LocalDate.of(1970, 1, 1));
        contract.setStatus("Status");
        Optional<Contract> ofResult = Optional.of(contract);

        ContractRepository contractRepository = mock(ContractRepository.class);
        when(contractRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ContractService contractService = new ContractService(contractRepository);
        EmployeeService employeeService = new EmployeeService(mock(EmployeeRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);
        ConcurrentModel model = new ConcurrentModel();

        // Act
        String actualDetailResult = contractController.detail(1L, model);

        // Assert
        verify(contractRepository).findById(1L);
        assertEquals("contract/detail", actualDetailResult);
        assertEquals(1, model.size());
        assertSame(contract, model.get("contract"));
    }

    /**
     * Test {@link ContractController#detail(Long, Model)}.
     *
     * <ul>
     *   <li>Given {@link ContractService} {@link ContractService#findById(Long)} return {@link
     *       Contract#Contract()}.
     *   <li>Then calls {@link ContractService#findById(Long)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#detail(Long, Model)}
     */
    @Test
    @DisplayName(
            "Test detail(Long, Model); given ContractService findById(Long) return Contract(); then calls findById(Long)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.detail(Long, Model)"})
    void testDetail_givenContractServiceFindByIdReturnContract_thenCallsFindById() {
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

        Contract contract = new Contract();
        contract.setContractNumber("42");
        contract.setContractType("Contract Type");
        contract.setDuration(1);
        contract.setEmployee(employee);
        contract.setEndDate(LocalDate.of(1970, 1, 1));
        contract.setId(1L);
        contract.setSalary(10.0d);
        contract.setSignedDate(LocalDate.of(1970, 1, 1));
        contract.setStartDate(LocalDate.of(1970, 1, 1));
        contract.setStatus("Status");

        ContractService contractService = mock(ContractService.class);
        when(contractService.findById(Mockito.<Long>any())).thenReturn(contract);
        EmployeeService employeeService = new EmployeeService(mock(EmployeeRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);
        ConcurrentModel model = new ConcurrentModel();

        // Act
        String actualDetailResult = contractController.detail(1L, model);

        // Assert
        verify(contractService).findById(1L);
        assertEquals("contract/detail", actualDetailResult);
        assertEquals(1, model.size());
        assertSame(contract, model.get("contract"));
    }

    /**
     * Test {@link ContractController#delete(Long)}.
     *
     * <ul>
     *   <li>Given {@link ContractRepository} {@link ContractRepository#deleteById(Object)} does
     *       nothing.
     *   <li>Then calls {@link ContractRepository#deleteById(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#delete(Long)}
     */
    @Test
    @DisplayName(
            "Test delete(Long); given ContractRepository deleteById(Object) does nothing; then calls deleteById(Object)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.delete(Long)"})
    void testDelete_givenContractRepositoryDeleteByIdDoesNothing_thenCallsDeleteById() {
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

        Contract contract = new Contract();
        contract.setContractNumber("42");
        contract.setContractType("Contract Type");
        contract.setDuration(1);
        contract.setEmployee(employee);
        contract.setEndDate(LocalDate.of(1970, 1, 1));
        contract.setId(1L);
        contract.setSalary(10.0d);
        contract.setSignedDate(LocalDate.of(1970, 1, 1));
        contract.setStartDate(LocalDate.of(1970, 1, 1));
        contract.setStatus("Status");
        Optional<Contract> ofResult = Optional.of(contract);

        ContractRepository contractRepository = mock(ContractRepository.class);
        doNothing().when(contractRepository).deleteById(Mockito.<Long>any());
        when(contractRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ContractService contractService = new ContractService(contractRepository);
        EmployeeService employeeService = new EmployeeService(mock(EmployeeRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);

        // Act
        String actualDeleteResult = contractController.delete(1L);

        // Assert
        verify(contractRepository).deleteById(1L);
        verify(contractRepository).findById(1L);
        assertEquals("redirect:/contracts/employee/1", actualDeleteResult);
    }

    /**
     * Test {@link ContractController#delete(Long)}.
     *
     * <ul>
     *   <li>Given {@link ContractService} {@link ContractService#delete(Long)} does nothing.
     *   <li>Then calls {@link ContractService#delete(Long)}.
     * </ul>
     *
     * <p>Method under test: {@link ContractController#delete(Long)}
     */
    @Test
    @DisplayName(
            "Test delete(Long); given ContractService delete(Long) does nothing; then calls delete(Long)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String ContractController.delete(Long)"})
    void testDelete_givenContractServiceDeleteDoesNothing_thenCallsDelete() {
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

        Contract contract = new Contract();
        contract.setContractNumber("42");
        contract.setContractType("Contract Type");
        contract.setDuration(1);
        contract.setEmployee(employee);
        contract.setEndDate(LocalDate.of(1970, 1, 1));
        contract.setId(1L);
        contract.setSalary(10.0d);
        contract.setSignedDate(LocalDate.of(1970, 1, 1));
        contract.setStartDate(LocalDate.of(1970, 1, 1));
        contract.setStatus("Status");

        ContractService contractService = mock(ContractService.class);
        doNothing().when(contractService).delete(Mockito.<Long>any());
        when(contractService.findById(Mockito.<Long>any())).thenReturn(contract);
        EmployeeService employeeService = new EmployeeService(mock(EmployeeRepository.class));

        ContractController contractController =
                new ContractController(contractService, employeeService);

        // Act
        String actualDeleteResult = contractController.delete(1L);

        // Assert
        verify(contractService).delete(1L);
        verify(contractService).findById(1L);
        assertEquals("redirect:/contracts/employee/1", actualDeleteResult);
    }
}
