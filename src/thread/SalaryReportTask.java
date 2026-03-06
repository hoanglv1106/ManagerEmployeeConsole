package thread;

import model.Department;
import model.Employee;
import service.EmployeeService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

public class SalaryReportTask implements Runnable {

    private final EmployeeService service;
    // Format thời gian in ra cho đẹp mắt
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public SalaryReportTask(EmployeeService service) {
        this.service = service;
    }

    @Override
    public void run() {
        try {
            printReport();
        } catch (Exception e) {
            // Bắt lỗi tại đây để đảm bảo Scheduled Thread không bị chết (terminate) nếu có lỗi bất ngờ
            System.err.println("[SalaryReportTask] Lỗi khi in báo cáo: " + e.getMessage());
        }
    }

    private void printReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     BÁO CÁO THỐNG KÊ LƯƠNG TỰ ĐỘNG - " + LocalDateTime.now().format(TIME_FORMATTER));
        System.out.println("=".repeat(60));

        List<Employee> allEmployees = service.getAll();

        // Nếu chưa có nhân viên nào thì thông báo nhẹ nhàng rồi thoát, tránh in ra số Infinity
        if (allEmployees.isEmpty()) {
            System.out.println("  [!] Hiện tại chưa có dữ liệu nhân viên nào trong hệ thống.");
            System.out.println("=".repeat(60) + "\n");
            return;
        }

        // Thống kê tổng
        DoubleSummaryStatistics stats = service.getSalaryStatistics();
        System.out.printf("  Tổng số nhân viên : %d%n", stats.getCount());
        System.out.printf("  Tổng quỹ lương    : %,.0f VNĐ%n", stats.getSum());
        System.out.printf("  Mức lương TB      : %,.0f VNĐ%n", stats.getAverage());
        System.out.printf("  Lương thấp nhất   : %,.0f VNĐ%n", stats.getMin());
        System.out.printf("  Lương cao nhất    : %,.0f VNĐ%n", stats.getMax());

        // Top 3 lương cao nhất
        System.out.println("\n  --- Top 3 nhân viên lương cao nhất ---");
        List<Employee> top3 = service.getTop3BySalary();
        for (int i = 0; i < top3.size(); i++) {
            Employee emp = top3.get(i);
            System.out.printf("  %d. %-20s | %,.0f VNĐ%n", i + 1, emp.getName(), emp.getSalary());
        }

        // Thống kê theo phòng ban
        System.out.println("\n  --- Thống kê chi tiết theo phòng ban ---");
        Map<Department, DoubleSummaryStatistics> byDept = service.getSalaryStatsByDepartment();
        byDept.forEach((dept, s) -> {
            if (s.getCount() > 0) { // Chỉ in những phòng ban có người
                System.out.printf("  %-12s | Số NV: %2d | Lương TB: %,.0f VNĐ%n",
                        dept.name(), s.getCount(), s.getAverage());
            }
        });

        // Đếm ACTIVE
        System.out.printf("%n  => Số lượng nhân viên đang ACTIVE: %d%n", service.countActive());
        System.out.println("=".repeat(60) + "\n");
    }
}