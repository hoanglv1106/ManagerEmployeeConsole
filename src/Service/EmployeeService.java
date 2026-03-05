package service;

import Model.Department;
import Model.Employee;
import Model.EmployeeStatus;
import Model.EmployeeType;
import exception.DataCorruptionException;
import exception.DuplicateIdExeption;
import repository.EmployeeRepository;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeService {

    private final EmployeeRepository repository;

    //  CONSTRUCTOR

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    //  CRUD

    public void add(Employee employee) throws DuplicateIdExeption, DataCorruptionException {
        if (repository.existsById(employee.getId())) {
            throw new DuplicateIdExeption("ID '" + employee.getId() + "' đã tồn tại.");
        }
        repository.add(employee);
        repository.saveData();
    }

    public boolean update(Employee updated) throws DataCorruptionException {
        boolean result = repository.update(updated);
        if (result) {
            repository.saveData();
        }
        return result;
    }

    public boolean remove(String id) throws DataCorruptionException {
        boolean result = repository.delete(id);
        if (result) {
            repository.saveData();
        }
        return result;
    }

    public Employee findById(String id) {
        return repository.findById(id);
    }

    public List<Employee> getAll() {
        return repository.getAll();
    }

    //  SORT

    public List<Employee> sortByName() {
        return repository.getAll().stream()
                .sorted(Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Employee> sortBySalaryDesc() {
        return repository.getAll().stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .collect(Collectors.toList());
    }

    public List<Employee> sortByHireDateAsc() {
        return repository.getAll().stream()
                .sorted(Comparator.comparing(Employee::getHireDate))
                .collect(Collectors.toList());
    }

    //  SEARCH

    public List<Employee> searchByName(String keyword) {
        String kw = keyword.trim().toLowerCase();
        return repository.getAll().stream()
                .filter(emp -> emp.getName().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public List<Employee> searchByDepartment(Department department) {
        return repository.getAll().stream()
                .filter(emp -> emp.getDepartment() == department)
                .collect(Collectors.toList());
    }
    // Tìm kiếm theo Loại nhân viên (FULL_TIME, PART_TIME, INTERN)
    public List<Employee> searchByType(EmployeeType type) {
        return repository.getAll().stream()
                .filter(emp -> emp.getType() == type)
                .collect(Collectors.toList());
    }

    // Tìm kiếm theo khoảng lương (Min - Max)
    public List<Employee> searchBySalaryRange(double minSalary, double maxSalary) {
        return repository.getAll().stream()
                .filter(emp -> emp.getSalary() >= minSalary && emp.getSalary() <= maxSalary)
                .collect(Collectors.toList());
    }

    //  FILTER

    /** Lọc nhân viên có lương > mức lương nhập vào */
    public List<Employee> filterByMinSalary(double minSalary) {
        return repository.getAll().stream()
                .filter(emp -> emp.getSalary() > minSalary)
                .collect(Collectors.toList());
    }

    public List<Employee> filterByStatus(EmployeeStatus status) {
        return repository.getAll().stream()
                .filter(emp -> emp.getStatus() == status)
                .collect(Collectors.toList());
    }

    //  TOP 3 LƯƠNG

    public List<Employee> getTop3BySalary() {
        return repository.getAll().stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    //  GROUP BY DEPARTMENT

    public Map<Department, List<Employee>> groupByDepartment() {
        return repository.getAll().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
    }

    //  ĐẾM NHÂN VIÊN ACTIVE
    public long countActive() {
        return repository.getAll().stream()
                .filter(emp -> emp.getStatus() == EmployeeStatus.ACTIVE)
                .count();
    }

    // THỐNG KÊ LƯƠNG

    /**
     * Trả về DoubleSummaryStatistics: count, sum, min, max, average
     */
    public DoubleSummaryStatistics getSalaryStatistics() {
        return repository.getAll().stream()
                .mapToDouble(Employee::getSalary)
                .summaryStatistics();
    }

    public Map<Department, DoubleSummaryStatistics> getSalaryStatsByDepartment() {
        return repository.getAll().stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.summarizingDouble(Employee::getSalary)
                ));
    }
}
