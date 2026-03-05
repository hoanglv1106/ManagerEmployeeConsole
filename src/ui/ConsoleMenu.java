package ui;

import Model.Department;
import Model.Employee;
import Model.EmployeeStatus;
import Model.EmployeeType;
import service.EmployeeService;
import exception.DataCorruptionException;
import exception.DuplicateIdExeption; // Đã sửa tên đúng chính tả
import exception.InvalidFormatException;
import exception.NegativeSalaryException;
import util.InputUtil;
import util.ValidationUtil;

import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

public class ConsoleMenu {

    private static final String RED   = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN  = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    private final EmployeeService service;

    // ==================== CONSTRUCTOR ====================

    public ConsoleMenu(EmployeeService service) {
        this.service = service;
    }

    // ==================== MAIN LOOP ====================

    public void start() {
        while (true) {
            printMainMenu();
            int choice = InputUtil.readInt("Chọn chức năng: ");
            switch (choice) {
                case 1  -> handleAdd();
                case 2  -> handleUpdate();
                case 3  -> handleRemove();
                case 4  -> handleFindById();
                case 5  -> handleListAll();
                case 6  -> handleSort();
                case 7  -> handleSearch();
                case 8  -> handleFilter();
                case 9  -> handleTop3();
                case 10 -> handleGroupByDept();
                case 11 -> handleCountActive();
                case 12 -> handleSalaryStats();
                case 0  -> {
                    System.out.println(GREEN + "Đã thoát chương trình. Tạm biệt!" + RESET);
                    return;
                }
                default -> printError("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        }
    }

    // ==================== MENU IN RA ====================

    private void printMainMenu() {
        System.out.println("\n" + CYAN + "=".repeat(45));
        System.out.println("       QUẢN LÝ NHÂN VIÊN - MENU CHÍNH");
        System.out.println("=".repeat(45) + RESET);
        System.out.println("  1.  Thêm nhân viên");
        System.out.println("  2.  Cập nhật nhân viên");
        System.out.println("  3.  Xóa nhân viên");
        System.out.println("  4.  Tìm theo ID");
        System.out.println("  5.  Hiển thị tất cả danh sách");
        System.out.println("  6.  Sắp xếp");
        System.out.println("  7.  Tìm kiếm theo tên / phòng ban");
        System.out.println("  8.  Lọc nhân viên");
        System.out.println("  9.  Top 3 lương cao nhất");
        System.out.println("  10. Nhóm theo phòng ban");
        System.out.println("  11. Đếm nhân viên ACTIVE");
        System.out.println("  12. Thống kê lương tổng thể");
        System.out.println("  0.  Thoát");
        System.out.println(CYAN + "=".repeat(45) + RESET);
    }

    // ==================== CASE 1: ADD ====================

    private void handleAdd() {
        System.out.println(CYAN + "\n--- THÊM NHÂN VIÊN ---" + RESET);
        try {
            String id = InputUtil.readString("Nhập ID       : ");
            if (id.isBlank()) throw new IllegalArgumentException("ID không được để trống.");

            String name = InputUtil.readString("Nhập tên      : ");
            if (name.isBlank()) throw new IllegalArgumentException("Tên không được để trống.");

            String email = InputUtil.readString("Nhập email    : ");
            ValidationUtil.validateEmail(email);

            String phone = InputUtil.readString("Nhập SĐT      : ");
            ValidationUtil.validatePhone(phone);

            double salary = InputUtil.readDouble("Nhập lương    : ");
            ValidationUtil.validateSalary(salary);

            LocalDate hireDate = InputUtil.readDate("Ngày vào (yyyy-MM-dd): ");

            Department dept       = InputUtil.readEnum("Phòng ban " + enumOptions(Department.values()) + ": ", Department.class);
            EmployeeType type     = InputUtil.readEnum("Loại NV " + enumOptions(EmployeeType.values()) + ": ", EmployeeType.class);
            EmployeeStatus status = InputUtil.readEnum("Trạng thái " + enumOptions(EmployeeStatus.values()) + ": ", EmployeeStatus.class);

            Employee emp = new Employee(id, name, email, phone, salary, hireDate, dept, type, status);
            service.add(emp);
            System.out.println(GREEN + "Thêm nhân viên thành công!" + RESET);

        } catch (DuplicateIdExeption | DataCorruptionException | InvalidFormatException | NegativeSalaryException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            printError("Dữ liệu không hợp lệ: " + e.getMessage());
        }
    }

    // ==================== CASE 2: UPDATE ====================

    private void handleUpdate() {
        System.out.println(CYAN + "\n--- CẬP NHẬT NHÂN VIÊN ---" + RESET);
        try {
            String id = InputUtil.readString("Nhập ID cần cập nhật: ");
            Employee existing = service.findById(id);
            if (existing == null) {
                printError("Không tìm thấy nhân viên với ID: " + id);
                return;
            }
            System.out.println("Đang cập nhật cho nhân viên: " + existing.getName());

            String name = InputUtil.readString("Tên mới       : ");
            if (name.isBlank()) throw new IllegalArgumentException("Tên không được để trống.");

            String email = InputUtil.readString("Email mới     : ");
            ValidationUtil.validateEmail(email);

            String phone = InputUtil.readString("SĐT mới       : ");
            ValidationUtil.validatePhone(phone);

            double salary = InputUtil.readDouble("Lương mới     : ");
            ValidationUtil.validateSalary(salary);

            LocalDate hireDate = InputUtil.readDate("Ngày vào (yyyy-MM-dd): ");

            Department dept       = InputUtil.readEnum("Phòng ban " + enumOptions(Department.values()) + ": ", Department.class);
            EmployeeType type     = InputUtil.readEnum("Loại NV " + enumOptions(EmployeeType.values()) + ": ", EmployeeType.class);
            EmployeeStatus status = InputUtil.readEnum("Trạng thái " + enumOptions(EmployeeStatus.values()) + ": ", EmployeeStatus.class);

            Employee updated = new Employee(id, name, email, phone, salary, hireDate, dept, type, status);
            boolean ok = service.update(updated);
            System.out.println(ok ? GREEN + "Cập nhật thành công!" + RESET : RED + "Không tìm thấy để cập nhật." + RESET);

        } catch (DataCorruptionException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            printError("Dữ liệu không hợp lệ: " + e.getMessage());
        }
    }

    // ==================== CASE 3: REMOVE ====================

    private void handleRemove() {
        System.out.println(CYAN + "\n--- XÓA NHÂN VIÊN ---" + RESET);
        try {
            String id = InputUtil.readString("Nhập ID cần xóa: ");
            boolean ok = service.remove(id);
            System.out.println(ok ? GREEN + "Xóa thành công!" + RESET : RED + "Không tìm thấy nhân viên với ID: " + id + RESET);
        } catch (DataCorruptionException e) {
            printError(e.getMessage());
        }
    }

    // ==================== CASE 4: FIND BY ID ====================

    private void handleFindById() {
        System.out.println(CYAN + "\n--- TÌM THEO ID ---" + RESET);
        String id = InputUtil.readString("Nhập ID: ");
        Employee emp = service.findById(id);
        if (emp == null) {
            printError("Không tìm thấy nhân viên với ID: " + id);
        } else {
            printEmployeeList(List.of(emp));
        }
    }

    // ==================== CASE 5: LIST ALL ====================

    private void handleListAll() {
        System.out.println(CYAN + "\n--- DANH SÁCH NHÂN VIÊN ---" + RESET);
        List<Employee> all = service.getAll();
        printEmployeeList(all);
    }

    // ==================== CASE 6: SORT ====================

    private void handleSort() {
        System.out.println(CYAN + "\n--- SẮP XẾP ---" + RESET);
        System.out.println("  1. Theo tên (A-Z)");
        System.out.println("  2. Theo lương (cao → thấp)");
        System.out.println("  3. Theo ngày tuyển dụng (cũ → mới)");
        int choice = InputUtil.readInt("Chọn: ", 1, 3);
        List<Employee> result = switch (choice) {
            case 1 -> service.sortByName();
            case 2 -> service.sortBySalaryDesc();
            case 3 -> service.sortByHireDateAsc();
            default -> List.of();
        };
        printEmployeeList(result);
    }

    // ==================== CASE 7: SEARCH ====================

    private void handleSearch() {
        System.out.println(CYAN + "\n--- TÌM KIẾM ---" + RESET);
        System.out.println("  1. Theo tên");
        System.out.println("  2. Theo phòng ban");
        System.out.println("  3. Theo loại nhân viên");
        int choice = InputUtil.readInt("Chọn: ", 1, 3);
        List<Employee> result = switch (choice) {
            case 1 -> {
                String kw = InputUtil.readString("Nhập từ khóa tên: ");
                yield service.searchByName(kw);
            }
            case 2 -> {
                Department dept = InputUtil.readEnum("Phòng ban " + enumOptions(Department.values()) + ": ", Department.class);
                yield service.searchByDepartment(dept);
            }
            case 3 -> {
                EmployeeType type = InputUtil.readEnum("Loại NV " + enumOptions(EmployeeType.values()) + ": ", EmployeeType.class);
                yield service.searchByType(type);
            }
            default -> List.of();
        };
        printEmployeeList(result);
    }

    // ==================== CASE 8: FILTER ====================

    private void handleFilter() {
        System.out.println(CYAN + "\n--- LỌC NHÂN VIÊN ---" + RESET);
        System.out.println("  1. Theo mức lương tối thiểu (VD: Lương > input)");
        System.out.println("  2. Theo trạng thái (ACTIVE/INACTIVE)");
        int choice = InputUtil.readInt("Chọn: ", 1, 2);
        List<Employee> result;
        if (choice == 1) {
            double minSalary = InputUtil.readPositiveDouble("Nhập mức lương tối thiểu muốn lọc (VNĐ): ");
            result = service.filterByMinSalary(minSalary);
        } else {
            EmployeeStatus status = InputUtil.readEnum("Trạng thái " + enumOptions(EmployeeStatus.values()) + ": ", EmployeeStatus.class);
            result = service.filterByStatus(status);
        }
        printEmployeeList(result);
    }

    // ==================== CASE 9: TOP 3 ====================

    private void handleTop3() {
        System.out.println(CYAN + "\n--- TOP 3 LƯƠNG CAO NHẤT ---" + RESET);
        printEmployeeList(service.getTop3BySalary());
    }

    // ==================== CASE 10: GROUP BY DEPT ====================

    private void handleGroupByDept() {
        System.out.println(CYAN + "\n--- NHÓM THEO PHÒNG BAN ---" + RESET);
        Map<Department, List<Employee>> grouped = service.groupByDepartment();
        if (grouped.isEmpty()) {
            System.out.println("  Không có dữ liệu.");
            return;
        }
        grouped.forEach((dept, list) -> {
            System.out.println("\n  Phòng: " + CYAN + dept.name() + RESET + " (" + list.size() + " người)");
            printEmployeeList(list);
        });
    }

    // ==================== CASE 11: COUNT ACTIVE ====================

    private void handleCountActive() {
        long count = service.countActive();
        System.out.println(GREEN + "\nSố nhân viên đang ACTIVE: " + count + RESET);
    }

    // ==================== CASE 12: SALARY STATS ====================

    private void handleSalaryStats() {
        System.out.println(CYAN + "\n--- THỐNG KÊ LƯƠNG TỔNG THỂ ---" + RESET);
        DoubleSummaryStatistics totalStats = service.getSalaryStatistics();
        if (totalStats.getCount() == 0) {
            System.out.println("  Không có dữ liệu.");
            return;
        }

        System.out.printf("  Tổng NV   : %d%n", totalStats.getCount());
        System.out.printf("  Lương TB  : %,.0f VNĐ%n", totalStats.getAverage());
        System.out.printf("  Tổng lương: %,.0f VNĐ%n", totalStats.getSum());
        System.out.printf("  Cao nhất  : %,.0f VNĐ%n", totalStats.getMax());

        System.out.println(CYAN + "\nThống kê chi tiết theo phòng ban:" + RESET);
        Map<Department, DoubleSummaryStatistics> stats = service.getSalaryStatsByDepartment();
        stats.forEach((dept, s) ->
                System.out.printf("  %-12s | NV: %2d | TB: %,.0f | Min: %,.0f | Max: %,.0f%n",
                        dept.name(), s.getCount(), s.getAverage(), s.getMin(), s.getMax())
        );
    }

    // ==================== HELPERS ====================

    private void printError(String msg) {
        System.out.println(RED + "[LỖI] " + msg + RESET);
    }

    /**
     * Hàm dùng chung để in danh sách nhân viên ra Console.
     * Tách biệt logic in ấn khỏi Service.
     */
    private void printEmployeeList(List<Employee> list) {
        if (list == null || list.isEmpty()) {
            System.out.println(RED + "  [!] Không có dữ liệu phù hợp." + RESET);
            return;
        }
        for (Employee emp : list) {
            System.out.println("  - " + emp.toString());
        }
    }

    private <T extends Enum<T>> String enumOptions(T[] values) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i].name());
            if (i < values.length - 1) sb.append("/");
        }
        sb.append("]");
        return sb.toString();
    }
}