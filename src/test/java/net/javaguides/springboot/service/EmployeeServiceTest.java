package net.javaguides.springboot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.javaguides.springboot.exception.ResourceNotFoundException;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.repository.EmployeeRepository;
import net.javaguides.springboot.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;
  @InjectMocks
  private EmployeeServiceImpl employeeService;

  private Employee employee1;

  @BeforeEach
  public void setup() {
//    employeeRepository = mock(EmployeeRepository.class);
//    employeeService = new EmployeeServiceImpl(employeeRepository);

    employee1 =
        Employee.builder()
            .id(1)
            .firstName("Nicolai")
            .lastName("Mar")
            .email("marunev@gmail.com")
            .build();
  }

  @Test
  public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObject() {
    // given
    given(employeeRepository.findByEmail(employee1.getEmail()))
        .willReturn(Optional.empty());

    given(employeeRepository.save(employee1)).willReturn(employee1);
    // when
    Employee savedEmployee = employeeService.saveEmployee(employee1);

    //then

    assertThat(savedEmployee).isNotNull();
  }


  @Test
  public void givenExistingEmail_whenSaveEmployee_thenThrowsException() {
    // given
    given(employeeRepository.findByEmail(employee1.getEmail()))
        .willReturn(Optional.of(employee1));

    // when
    assertThrows(ResourceNotFoundException.class,
        () -> employeeService.saveEmployee(employee1));

    //then
    verify(employeeRepository, never()).save(any(Employee.class));
  }

  @Test
  public void givenEmployeeList_whenFindAllEmployees_thenReturnEmployeeList() {
    // given
    Employee employee2 =
        Employee.builder()
            .id(2)
            .firstName("Second Employee")
            .lastName("Second LastName")
            .email("second@gmail.com")
            .build();

    given(employeeRepository.findAll()).willReturn(List.of(employee1, employee2));

    // when

    List<Employee> employeeList = employeeService.getAllEmployees();

    //then
    assertThat(employeeList).isNotNull();
    assertThat(employeeList).hasSize(2);
  }


  @Test
  public void givenEmptyEmployeeList_whenFindAllEmployees_thenReturnEmptyEmployeeList() {
    // given
    given(employeeRepository.findAll()).willReturn(Collections.emptyList());

    // when

    List<Employee> employeeList = employeeService.getAllEmployees();

    //then
    assertThat(employeeList).isEmpty();
    assertThat(employeeList).hasSize(0);
  }


  @Test
  public void givenEmployee_whenFindById_thenReturnEmployee() {
    // given
    given(employeeRepository.findById(1L)).willReturn(Optional.of(employee1));

    // when
    Employee employeeById = employeeService.getEmployeeById(employee1.getId()).get();

    //then
    assertThat(employeeById).isNotNull();
    assertThat(employeeById.getFirstName()).isEqualTo("Nicolai");
  }


  @Test
  public void givenEmployeeObject_whenUpdateEmployee_thenReturnEmployeeObject() {
    // given
    Employee newEmployee =
        Employee.builder()
            .id(1L)
            .firstName("Second Employee")
            .lastName("Second LastName")
            .email("second@gmail.com")
            .build();


    given(employeeRepository.save(any(Employee.class))).willAnswer(invoke -> invoke.getArgument(0));

    // when
    Employee updatedEmployee = employeeService.updateEmployee(newEmployee);

    //then
    assertThat(updatedEmployee).isNotNull();
    assertThat(updatedEmployee.getLastName()).isEqualTo("Second LastName");
  }


  @Test
  public void givenEmployeeObject_whenDeleteEmployee_thenEmployeeIsDeleted() {
    // given
    long employeeId = 1L;
    willDoNothing().given(employeeRepository).deleteById(employeeId);
    // when
    employeeService.deleteEmployee(employeeId);
    //then
    verify(employeeRepository, times(1)).deleteById(employeeId);
  }

}
