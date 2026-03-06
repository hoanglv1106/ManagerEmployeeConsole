package repository.io;

import model.Department;
import model.Employee;
import model.EmployeeStatus;
import model.EmployeeType;
import config.FileConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvFileReader {

    public List<Employee> read() {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FileConfig.FILE_PATH))) {
            String line;
            boolean isHeader = true;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] fields = line.split(",");

                if (fields.length < 9) {
                    System.err.println("[CsvFileReader] Dòng " + lineNumber + " thiếu trường, bỏ qua: " + line);
                    continue;
                }

                try {
                    String id             = fields[0].trim();
                    String name           = fields[1].trim();
                    String email          = fields[2].trim();
                    String phone          = fields[3].trim();
                    double salary         = Double.parseDouble(fields[4].trim());
                    LocalDate hireDate    = LocalDate.parse(fields[5].trim());
                    Department department = Department.valueOf(fields[6].trim().toUpperCase());
                    EmployeeType type     = EmployeeType.valueOf(fields[7].trim().toUpperCase());
                    EmployeeStatus status = EmployeeStatus.valueOf(fields[8].trim().toUpperCase());

                    employees.add(new Employee(id, name, email, phone, salary, hireDate, department, type, status));

                } catch (NumberFormatException e) {
                    System.err.println("[CsvFileReader] Dòng " + lineNumber + " - Lỗi parse salary: " + e.getMessage());
                } catch (DateTimeParseException e) {
                    System.err.println("[CsvFileReader] Dòng " + lineNumber + " - Lỗi parse hireDate (yyyy-MM-dd): " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("[CsvFileReader] Dòng " + lineNumber + " - Lỗi parse enum (Department/Type/Status): " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("[CsvFileReader] Không thể đọc file: " + FileConfig.FILE_PATH + " | " + e.getMessage());
        }

        return employees;
    }
}

