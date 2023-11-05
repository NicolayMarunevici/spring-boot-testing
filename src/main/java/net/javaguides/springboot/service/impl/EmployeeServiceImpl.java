package net.javaguides.springboot.service.impl;

import java.util.List;
import java.util.Optional;
import net.javaguides.springboot.exception.ResourceNotFoundException;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.repository.EmployeeRepository;
import net.javaguides.springboot.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;

  public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public Employee saveEmployee(Employee employee) {
    Optional<Employee> employeeOptional = employeeRepository.findByEmail(employee.getEmail());

    if (employeeOptional.isPresent()) {
      throw new ResourceNotFoundException("Such Employee is already exist");
    }
    return employeeRepository.save(employee);
  }

  @Override
  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  @Override
  public Optional<Employee> getEmployeeById(Long id) {
    return employeeRepository.findById(id);
  }

  @Override
  public Employee updateEmployee(Employee employee) {
    return employeeRepository.save(employee);
  }

  @Override
  public void deleteEmployee(long id) {
     employeeRepository.deleteById(id);
  }
}
