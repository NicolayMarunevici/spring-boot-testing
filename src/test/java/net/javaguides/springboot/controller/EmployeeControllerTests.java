package net.javaguides.springboot.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
public class EmployeeControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmployeeService employeeService;

  @Autowired
  private ObjectMapper objectMapper;

  Employee employee;

  @BeforeEach
  public void setup() {
    employee =
        Employee.builder()
            .id(1)
            .firstName("Nicolai")
            .lastName("Mar")
            .email("marunev@gmail.com")
            .build();
  }


  // Create
  @Test
  public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmpoyee()
      throws Exception {
    // given
    given(employeeService.saveEmployee(any(Employee.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(employee)));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.firstName", is(employee.getFirstName())))
        .andExpect(
            jsonPath("$.lastName", is(employee.getLastName())))
        .andExpect(jsonPath("$.email", is(employee.getEmail())));
  }


  // GetAll
  @Test
  public void givenListOfEmployees_whenGetAllEmployee_thenReturnEmpoyeeList()
      throws Exception {

    List<Employee> employeeList =
        List.of(employee, new Employee(1L, "NewNicolai", "NewMar", "newEmail"));
    // given
    given(employeeService.getAllEmployees())
        .willReturn(employeeList);

    // when
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$[0].firstName",
                is(employee.getFirstName())))
        .andExpect(
            jsonPath("$[0].lastName",
                is(employee.getLastName())))
        .andExpect(
            jsonPath("$[0].email",
                is(employee.getEmail())))
        .andExpect(
            jsonPath("$.size()", is(employeeList.size())));
  }


  //GetById Positive
  @Test
  public void givenEmployeeObject_whenGetById_thenReturnEmployee() throws Exception {
    // given
    long employeeId = 1L;
    given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee));
    // when
    ResultActions response = mockMvc.perform(
        MockMvcRequestBuilders.get("/api/employees/{id}", employeeId) // Вставляются Path Variables
            .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.firstName",
                is(employee.getFirstName())))
        .andExpect(
            jsonPath("$.lastName",
                is(employee.getLastName())))
        .andExpect(
            jsonPath("$.email",
                is(employee.getEmail())));
  }


  //GetById Negative
  @Test
  public void givenEmptyObject_whenGetById_thenReturnNotFound() throws Exception {
    // given
    long employeeId = 1L;
    given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());
    // when
    ResultActions response = mockMvc.perform(
        MockMvcRequestBuilders.get("/api/employees/", employeeId) // Вставляются Path Variables
            .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isNotFound());
  }


  @Test
  public void givenUpdatedEmployee_whenUpdatedEmployee_thenReturnUpdateEmployeeObject()
      throws Exception {
    // given
    long employeeId = 1L;

    Employee updatedEmployee =
        Employee.builder()
            .firstName("Second Employee")
            .lastName("Second LastName")
            .email("second@gmail.com")
            .build();

    // when
    given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee));
    given(employeeService.updateEmployee(any(Employee.class))).willAnswer(
        invoke -> invoke.getArgument(0));


    ResultActions response =
        mockMvc.perform(MockMvcRequestBuilders.put("/api/employees/{id}", employeeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedEmployee)));

    // then
    response
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
        .andExpect(
            jsonPath("$.lastName", is(updatedEmployee.getLastName())))
        .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
  }


  @Test
  public void givenUpdatedEmployee_whenUpdatedEmployee_thenReturn404()
      throws Exception {
    // given
    long employeeId = 1L;

    Employee updatedEmployee =
        Employee.builder()
            .firstName("Second Employee")
            .lastName("Second LastName")
            .email("second@gmail.com")
            .build();

    // when
    given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());
    given(employeeService.updateEmployee(any(Employee.class))).willAnswer(
        invoke -> invoke.getArgument(0));


    ResultActions response =
        mockMvc.perform(MockMvcRequestBuilders.put("/api/employees/{id}", employeeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedEmployee)));

    // then
    response
        .andExpect(status().isNotFound());
  }

  @Test
  public void givenEmployee_whenDeleteEmployee_thenEmployeeIsDeleted() throws Exception {
    // given
    long employeeId = 1L;
    willDoNothing().given(employeeService).deleteEmployee(employeeId);

    // when
    ResultActions response =
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/employees/{id}", employeeId)
            .contentType(MediaType.APPLICATION_JSON));

    // then
    response
        .andExpect(status().isOk());
  }
}
