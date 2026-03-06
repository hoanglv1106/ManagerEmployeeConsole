package model;


import java.time.LocalDate;

public class Employee {
    private String id;
    private String name;
    private String email;
    private String phone;
    private double salary;
    private LocalDate hireDate;
    private Department department;
    private EmployeeType type;
    private EmployeeStatus status;

    public Employee(String id, String name, String email, String phone,
                    double salary, LocalDate hireDate,
                    Department department, EmployeeType type, EmployeeStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.salary = salary;
        this.hireDate = hireDate;
        this.department = department;
        this.type = type;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public EmployeeType getType() {
        return type;
    }

    public void setType(EmployeeType type) {
        this.type = type;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }




    @Override
    public String toString() {
        return String.format(
                "[%s] %s | %s | %s | %.2f | %s | %s | %s | %s",
                id, name, email, phone, salary,
                hireDate, department, type, status
        );
    }
}
