package repository;

import model.Employee;
import config.FileConfig;
import exception.DataCorruptionException;
import repository.io.AtomicFileWriter;
import repository.io.CsvFileReader;
import repository.io.RetryExecutor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EmployeeRepository {

    private final CopyOnWriteArrayList<Employee> employees = new CopyOnWriteArrayList<>();
    private final CsvFileReader csvFileReader = new CsvFileReader();
    private final AtomicFileWriter atomicFileWriter = new AtomicFileWriter();

    // LOAD

    public void loadData() {
        List<Employee> loaded = csvFileReader.read();
        employees.clear();
        employees.addAll(loaded);
        System.out.println("[Repository] Đã load " + employees.size() + " nhân viên từ " + FileConfig.FILE_PATH);
    }

    //SAVE

    public void saveData() throws DataCorruptionException {
        RetryExecutor.execute(
                () -> atomicFileWriter.write(employees),
                "saveData"
        );
        System.out.println("[Repository] Đã lưu " + employees.size() + " nhân viên vào " + FileConfig.FILE_PATH);
    }

    //CRUD

    public List<Employee> getAll() {
        return Collections.unmodifiableList(employees);
    }

    public void add(Employee employee) {
        employees.add(employee);
    }

    public boolean update(Employee updated) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId().equals(updated.getId())) {
                employees.set(i, updated);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String id) {
        return employees.removeIf(emp -> emp.getId().equals(id));
    }

    public Employee findById(String id) {
        return employees.stream()
                .filter(emp -> emp.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean existsById(String id) {
        return employees.stream().anyMatch(emp -> emp.getId().equals(id));
    }
}
