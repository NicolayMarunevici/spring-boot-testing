package net.javaguides.springboot.integration.testcontainer;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
//@Testcontainers
public class EmployeeControllerIT extends AbstractionBaseTest{

//  @Container
//  private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest")
//      .withUsername("username")
//      .withPassword("password")
//      .withDatabaseName("ems");
//
//  @DynamicPropertySource
//  public static void dynamicPropertySource(DynamicPropertyRegistry registry){
//    registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
//    registry.add("spring.datasource.username", mySQLContainer::getUsername);
//    registry.add("spring.datasource.password", mySQLContainer::getPassword);
//  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private ObjectMapper objectMapper;
  Employee employee;

  @BeforeEach
  void setup() {
    employeeRepository.deleteAll();

    employee =
        Employee.builder()
            .firstName("Nicolai")
            .lastName("Mar")
            .email("marunev123@gmail.com")
            .build();
  }


  @Test
  public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmpoyee()
      throws Exception {

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

  @Test
  public void givenListOfEmployees_whenGetAllEmployee_thenReturnEmpoyeeList()
      throws Exception {

    // given
    Employee newEmployee =
        Employee.builder()
            .firstName("Nicolai")
            .lastName("Mar")
            .email("marunev123@gmail.com")
            .build();

    List<Employee> employeeList =
        List.of(new Employee(1L, "NewNicolai", "NewMar", "newEmail"), newEmployee);
    employeeRepository.saveAll(employeeList);

    // when
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print()) // выводит в консоль значения
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$[0].firstName",
                is(employeeList.get(0).getFirstName())))
        .andExpect(
            jsonPath("$[0].lastName",
                is(employeeList.get(0).getLastName())))
        .andExpect(
            jsonPath("$[0].email",
                is(employeeList.get(0).getEmail())))
        .andExpect(
            jsonPath("$.size()", is(employeeList.size())));
  }


  @Test
  public void givenEmployeeObject_whenGetById_thenReturnEmployee() throws Exception {
    // given
    employeeRepository.save(employee);
    // when
    ResultActions response = mockMvc.perform(
        MockMvcRequestBuilders.get("/api/employees/{id}",
                employee.getId()) // Вставляются Path Variables
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


  @Test
  public void givenEmptyObject_whenGetById_thenReturnNotFound() throws Exception {
    // given
    long employeeId = 1L;
    employeeRepository.save(employee);
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
    employeeRepository.save(employee);

    Employee updatedEmployee =
        Employee.builder()
            .firstName("Second Employee")
            .lastName("Second LastName")
            .email("second@gmail.com")
            .build();

    // when
    ResultActions response =
        mockMvc.perform(MockMvcRequestBuilders.put("/api/employees/{id}", employee.getId())
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
    employeeRepository.save(employee);

    Employee updatedEmployee =
        Employee.builder()
            .firstName("Second Employee")
            .lastName("Second LastName")
            .email("second@gmail.com")
            .build();

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
    employeeRepository.save(employee);

    // when
    ResultActions response =
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/employees/{id}", employee.getId())
            .contentType(MediaType.APPLICATION_JSON));

    // then
    response.andDo(print())
        .andExpect(status().isOk());
  }
}
