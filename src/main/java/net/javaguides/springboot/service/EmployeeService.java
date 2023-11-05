package net.javaguides.springboot.service;

import java.util.List;
import java.util.Optional;
import net.javaguides.springboot.model.Employee;

public interface EmployeeService {
  Employee saveEmployee(Employee employee);
  List<Employee> getAllEmployees();
  Optional<Employee> getEmployeeById(Long id);
  Employee updateEmployee(Employee employee);
  void deleteEmployee(long id);
}
