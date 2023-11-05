package net.javaguides.springboot.integration;

import java.util.List;
import java.util.Optional;
import net.javaguides.springboot.integration.testcontainer.AbstractionBaseTest;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.model.EmployeeDto;
import net.javaguides.springboot.repository.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryIT extends AbstractionBaseTest {

  @Autowired
  private EmployeeRepository employeeRepository;

  private Employee employee;

  @BeforeEach
  public void setup() {
    // given
    employee =
        Employee.builder()
            .firstName("Nicolai")
            .lastName("Mar")
            .email("marunev@gmail.com")
            .build();
  }

  // JUnit save
  @DisplayName("JUnit save method")
  @Test
  void givenEmployee_whenSave_thenSavedEmployee() {
    // given
//    Employee employee =
//        Employee.builder()
//            .firstName("Nicolai")
//            .lastName("Mar")
//            .email("marunev@gmail.com")
//            .build();
    // when
    Employee savedEmployee = employeeRepository.save(employee);
    //then
    Assertions.assertThat(savedEmployee).isNotNull();
    Assertions.assertThat(savedEmployee.getId()).isGreaterThan(0);
  }


  //JUnit findAll
  @DisplayName("JUnit findAll")
  @Test
  void givenEmployeeList_whenFindAll_thenReturnEmployeesList() {
    //given
    Employee employee2 =
        Employee.builder()
            .firstName("John")
            .lastName("Cena")
            .email("cena@gmail.com")
            .build();

    Employee employee3 =
        Employee.builder()
            .firstName("Kristian")
            .lastName("Ulmanu")
            .email("ulmanu@gmail.com")
            .build();

    employeeRepository.save(employee);
    employeeRepository.save(employee2);
    employeeRepository.save(employee3);

    //when
    List<Employee> employeeList = employeeRepository.findAll();

    //then
    Assertions.assertThat(employeeList).isNotNull();
    Assertions.assertThat(employeeList.size()).isEqualTo(3);
  }


  // JUnit find By Id
  @DisplayName("JUnit find By Id")
  @Test
  void givenEmployee_whenFindById_thenReturnEmployees() {
    //given
    Employee expectedEmployee =
        Employee.builder()
            .firstName("John")
            .lastName("Cena")
            .email("cena@gmail.com")
            .build();

    employeeRepository.save(expectedEmployee);

    //when
    Employee employeeById = employeeRepository.findById(expectedEmployee.getId()).orElseThrow(
        () -> new RuntimeException(
            String.format("Employee with id %s does not exist", expectedEmployee.getId())));

    //then
    Assertions.assertThat(employeeById).isNotNull();
    Assertions.assertThat(employeeById).isEqualTo(expectedEmployee);
  }


  // JUnit find By Email
  @DisplayName("JUnit find By Email")
  @Test
  void givenEmployee_whenFindByEmail_thenReturnEmployees() {
    //given
    Employee expectedEmployee =
        Employee.builder()
            .firstName("John")
            .lastName("Cena")
            .email("cena@gmail.com")
            .build();

    EmployeeDto employeeDto = new EmployeeDto("John", "Cena");

    employeeRepository.save(expectedEmployee);

    //when
    Employee actualEmployeeByEmail = employeeRepository.findByEmail(expectedEmployee.getEmail())
        .orElseThrow(() -> new RuntimeException(
            String.format("Employee with email %s does not exist", expectedEmployee.getEmail())));

    //then
    Assertions.assertThat(actualEmployeeByEmail).isNotNull();
    Assertions.assertThat(actualEmployeeByEmail).usingRecursiveComparison()
        .ignoringFields("id", "email").isEqualTo(employeeDto);
  }


  // JUnit update
  @DisplayName("JUnit update method")
  @Test
  void givenEmployee_whenUpdate_thenUpdateEmployee() {
    // given
//    Employee employee =
//        Employee.builder()
//            .id(1)
//            .firstName("Nicolai")
//            .lastName("Mar")
//            .email("marunev@gmail.com")
//            .build();

    // when
    employeeRepository.save(employee);

    Employee newEmployee = employeeRepository.findById(employee.getId()).get();
    newEmployee.setFirstName("UpdatedNicolai");
    newEmployee.setLastName("UpdatedMar");
    newEmployee.setEmail("UpdatedMarunev@gmail.com");

    Employee updatedEmployee = employeeRepository.save(newEmployee);

    //then
    Assertions.assertThat(updatedEmployee).isNotNull();
    Assertions.assertThat(updatedEmployee.getId()).isGreaterThan(0);
    Assertions.assertThat(updatedEmployee.getFirstName()).isEqualTo("UpdatedNicolai");
    Assertions.assertThat(updatedEmployee.getLastName()).isEqualTo("UpdatedMar");
    Assertions.assertThat(updatedEmployee.getEmail()).isEqualTo("UpdatedMarunev@gmail.com");
  }


  // JUnit delete
  @DisplayName("JUnit delete method")
  @Test
  void givenEmployee_whenDelete_thenDeleteEmployee() {
    // given
//    Employee employee =
//        Employee.builder()
//            .id(1)
//            .firstName("Nicolai")
//            .lastName("Mar")
//            .email("marunev@gmail.com")
//            .build();
    // when
    employeeRepository.save(employee);
    employeeRepository.delete(employee);
    Optional<Employee> findEmployeeById = employeeRepository.findById(employee.getId());

    //then
//    Assertions.assertThat(findEmployeeById).isNull();
    Assertions.assertThat(findEmployeeById).isEmpty();
  }


  // JUnit custom JPQL with index
  @DisplayName("JUnit custom JPQL with index")
  @Test
  void givenFirstNameAndLastName_whenFindByJPQL_thenReturnEmployeeObject() {
    // given
//    Employee employee =
//        Employee.builder()
//            .firstName("Nicolai")
//            .lastName("Mar")
//            .email("marunev@gmail.com")
//            .build();
    // when
    employeeRepository.save(employee);
    String firstName = "Nicolai";
    String lastName = "Mar";

    Employee savedEmployee = employeeRepository.findByJPQL(firstName, lastName);

    //then
    Assertions.assertThat(savedEmployee).isNotNull();
    Assertions.assertThat(savedEmployee).isNotNull();
    Assertions.assertThat(savedEmployee.getId()).isPositive();
    Assertions.assertThat(savedEmployee.getFirstName()).isEqualTo("Nicolai");
    Assertions.assertThat(savedEmployee.getLastName()).isEqualTo("Mar");
    Assertions.assertThat(savedEmployee.getEmail()).isEqualTo("marunev@gmail.com");
  }


  // JUnit custom JPQL with named parameters
  @DisplayName("JUnit custom JPQL with named parameters")
  @Test
  void givenFirstNameAndLastName_whenFindByJPQLNamedParams_thenReturnEmployeeObject() {
    // given
//    Employee employee =
//        Employee.builder()
//            .firstName("Nicolai")
//            .lastName("Mar")
//            .email("marunev@gmail.com")
//            .build();
    // when
    employeeRepository.save(employee);
    String firstName = "Nicolai";
    String lastName = "Mar";

    Employee savedEmployee = employeeRepository.findByJPQLNamedParam(firstName, lastName);

    //then
    Assertions.assertThat(savedEmployee).isNotNull();
    Assertions.assertThat(savedEmployee.getId()).isPositive();
    Assertions.assertThat(savedEmployee.getFirstName()).isEqualTo("Nicolai");
    Assertions.assertThat(savedEmployee.getLastName()).isEqualTo("Mar");
    Assertions.assertThat(savedEmployee.getEmail()).isEqualTo("marunev@gmail.com");
  }

  // JUnit custom native SQL query
  @DisplayName("JUnit custom native SQL query")
  @Test
  void givenFirstNameAndLastName_whenFindByNativeSQL_thenReturnEmployeeObject() {
    // given
//    Employee employee =
//        Employee.builder()
//            .firstName("Nicolai")
//            .lastName("Mar")
//            .email("marunev@gmail.com")
//            .build();
    // when
    employeeRepository.save(employee);
    String firstName = "Nicolai";
    String lastName = "Mar";

    Employee savedEmployee = employeeRepository.findByNativeSQL(firstName, lastName);

    //then
    Assertions.assertThat(savedEmployee).isNotNull();
    Assertions.assertThat(savedEmployee.getId()).isPositive();
    Assertions.assertThat(savedEmployee.getFirstName()).isEqualTo("Nicolai");
    Assertions.assertThat(savedEmployee.getLastName()).isEqualTo("Mar");
    Assertions.assertThat(savedEmployee.getEmail()).isEqualTo("marunev@gmail.com");
  }


  // JUnit custom native SQL query
  @DisplayName("JUnit custom native SQL query")
  @Test
  void givenFirstNameAndLastName_whenFindByNativeSQLWithNamedParameters_thenReturnEmployeeObject() {
    // given
//    Employee employee =
//        Employee.builder()
//            .firstName("Nicolai")
//            .lastName("Mar")
//            .email("marunev@gmail.com")
//            .build();
    // when
    employeeRepository.save(employee);
    String firstName = "Nicolai";
    String lastName = "Mar";

    Employee savedEmployee = employeeRepository.findByNativeSQLNamedParameters(firstName, lastName);

    //then
    Assertions.assertThat(savedEmployee).isNotNull();
    Assertions.assertThat(savedEmployee.getId()).isPositive();
    Assertions.assertThat(savedEmployee.getFirstName()).isEqualTo("Nicolai");
    Assertions.assertThat(savedEmployee.getLastName()).isEqualTo("Mar");
    Assertions.assertThat(savedEmployee.getEmail()).isEqualTo("marunev@gmail.com");
  }
}

