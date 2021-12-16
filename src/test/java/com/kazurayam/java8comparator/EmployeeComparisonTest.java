package com.kazurayam.java8comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeComparisonTest {
    private Employee[] employees;
    private Employee[] sortedEmployeesByName;
    private Employee[] sortedEmployeesByNameDesc;

    @BeforeEach
    public void setup() {
        employees = new Employee[] {
                new Employee("John", 25, 3000.0, 992200),
                new Employee("Ace", 22, 2000.0, 5924001),
                new Employee("Keith", 35, 4000.0, 3924401)
        };
        sortedEmployeesByName = new Employee[] {
                new Employee("Ace", 22, 2000.0, 5924001),
                new Employee("John", 25, 3000.0, 992200),
                new Employee("Keith", 35, 4000.0, 3924401)
        };
        sortedEmployeesByNameDesc = new Employee[] {
                new Employee("Keith", 35, 4000.0, 3924401),
                new Employee("John", 25, 3000.0, 992200),
                new Employee("Ace", 22, 2000.0, 5924001)
        };
    }

    @Test
    public void testComparingThenSortedByName() {
        Comparator<Employee> employeeNameComparator
                = Comparator.comparing(Employee::getName);
        Arrays.sort(employees, employeeNameComparator);
        System.out.println("employees=");
        for (Employee emp : employees) {
            System.out.println(emp.toString());
        };
        assertTrue(Arrays.equals(employees, sortedEmployeesByName));
    }

    @Test
    public void testComparingWithComparatorThenSortedByNameDesc() {
        Comparator<Employee> employeeNameComparator
                = Comparator.comparing(
                        Employee::getName, (s1, s2) -> {
                    return s2.compareTo(s1);  // descending order
                        });
        Arrays.sort(employees, employeeNameComparator);
        assertTrue(Arrays.equals(employees, sortedEmployeesByNameDesc));
    }

    @Test
    public void testReversed() {
        Comparator<Employee> employeeNameComparatorReversed =
                Comparator.comparing(Employee::getName).reversed();
        Arrays.sort(employees, employeeNameComparatorReversed);
        assertTrue(Arrays.equals(employees, sortedEmployeesByNameDesc));

    }
}
