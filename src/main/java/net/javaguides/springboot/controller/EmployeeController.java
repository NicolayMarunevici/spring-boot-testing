package net.javaguides.springboot.controller;

import java.util.List;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
  private EmployeeService employeeService;

  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Employee createEmployee(@RequestBody Employee employee) {
    return employeeService.saveEmployee(employee);
  }


  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Employee> getAllEmployees() {
    return employeeService.getAllEmployees();
  }


  @GetMapping("/{id}")
  public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
    return employeeService.getEmployeeById(id).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }


  @PutMapping("/{id}")
  public ResponseEntity<Employee> updateEmployee(@PathVariable long id, @RequestBody Employee employee) {
    return employeeService.getEmployeeById(id).map(newEmployee -> {
      newEmployee.setFirstName(employee.getFirstName());
      newEmployee.setLastName(employee.getLastName());
      newEmployee.setEmail(employee.getEmail());

      Employee updateEmployee = employeeService.updateEmployee(newEmployee);

      return new ResponseEntity<>(updateEmployee, HttpStatus.OK);
    }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("{id}")
  public ResponseEntity<String> deleteEmployee(@PathVariable long id){
    employeeService.deleteEmployee(id);

    return new ResponseEntity<String>("Employee was deleted", HttpStatus.OK);
  }
}




