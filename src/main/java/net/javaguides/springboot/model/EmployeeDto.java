package net.javaguides.springboot.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDto {
  private String firstName;
  private String lastName;
}
