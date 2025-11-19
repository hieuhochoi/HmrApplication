package com.example.hrmapplication.controller;

import com.example.hrmapplication.dto.ContractRequest;
import com.example.hrmapplication.entity.Contract;
import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.mapper.ContractMapper;
import com.example.hrmapplication.service.ContractService;
import com.example.hrmapplication.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ContractControllerTest {

    @Mock
    private ContractService contractService;

    @Mock
    private EmployeeService employeeService;

    private ContractMapper contractMapper;

    @InjectMocks
    private ContractController contractController;

    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<Contract> contractCaptor;

    @BeforeEach
    void setUp() {
        contractMapper = new ContractMapper();
        contractController = new ContractController(contractService, employeeService, contractMapper);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(contractController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void shouldRenderContractListByEmployee() throws Exception {
        Long employeeId = 10L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFullName("Nguyen Van C");

        when(employeeService.findById(employeeId)).thenReturn(employee);
        when(contractService.findByEmployeeId(employeeId)).thenReturn(List.of());

        mockMvc.perform(get("/contracts/employee/{employeeId}", employeeId))
                .andExpect(status().isOk())
                .andExpect(view().name("contract/list"))
                .andExpect(model().attribute("employeeId", employeeId))
                .andExpect(model().attribute("employeeName", "Nguyen Van C"))
                .andExpect(model().attributeExists("contracts"));

        verify(contractService).findByEmployeeId(employeeId);
    }

    @Test
    void shouldRedirectAfterSavingContract() throws Exception {
        Long employeeId = 5L;
        Employee employee = new Employee();
        employee.setId(employeeId);

        ContractRequest request = new ContractRequest();
        request.setEmployeeId(employeeId);
        request.setContractNumber("HD001");
        request.setContractType("Thử việc");
        request.setSignedDate(LocalDate.of(2024, 1, 1));
        request.setStartDate(LocalDate.of(2024, 1, 10));
        request.setDuration(2);
        request.setSalary(10000000.0);
        request.setStatus("ACTIVE");

        when(employeeService.findById(employeeId)).thenReturn(employee);
        when(contractService.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/contracts/save")
                        .flashAttr("contract", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contracts/employee/" + employeeId))
                .andExpect(flash().attributeExists("successMessage"));

        verify(contractService).save(contractCaptor.capture());
        assertThat(contractCaptor.getValue().getContractNumber()).isEqualTo("HD001");
    }

    @Test
    void shouldReturnFormWhenDuplicateContractNumber() throws Exception {
        Long employeeId = 8L;
        Employee employee = new Employee();
        employee.setId(employeeId);

        ContractRequest request = new ContractRequest();
        request.setEmployeeId(employeeId);
        request.setContractNumber("DUP001");
        request.setContractType("Thử việc");
        request.setSignedDate(LocalDate.of(2024, 2, 1));
        request.setStartDate(LocalDate.of(2024, 2, 15));
        request.setDuration(1);
        request.setSalary(8000000.0);
        request.setStatus("ACTIVE");

        when(employeeService.findById(employeeId)).thenReturn(employee);
        when(contractService.save(any(Contract.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        mockMvc.perform(post("/contracts/save")
                        .flashAttr("contract", request))
                .andExpect(status().isOk())
                .andExpect(view().name("contract/form"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(contractService).save(any(Contract.class));
    }
}

