import config.AppConfig; // Import cấu hình thời gian
import repository.EmployeeRepository;
import service.EmployeeService; // Đã sửa thành chữ thường
import thread.AutoSaveTask;
import thread.SalaryReportTask;
import ui.ConsoleMenu;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        System.out.println("Đang khởi động hệ thống Quản lý nhân viên...");

        //  KHỞI TẠO REPOSITORY
        EmployeeRepository repository = new EmployeeRepository();
        try {
            repository.loadData();
            // Nếu có dữ liệu sẽ in ra thông báo từ bên trong repository
        } catch (Exception e) {
            System.err.println("[Main] Lỗi khi nạp dữ liệu: " + e.getMessage());
            System.err.println("[Main] Tiếp tục với danh sách trống...");
        }

        //  KHỞI TẠO SERVICE
        EmployeeService service = new EmployeeService(repository);

        // SCHEDULED THREAD POOL
        // Dùng số luồng từ cấu hình chung
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(AppConfig.THREAD_POOL_SIZE);

        // AutoSaveTask: Delay lần đầu 60s, sau đó lặp lại mỗi 60s
        scheduler.scheduleAtFixedRate(
                new AutoSaveTask(repository),
                AppConfig.AUTO_SAVE_INTERVAL,
                AppConfig.AUTO_SAVE_INTERVAL,
                TimeUnit.SECONDS
        );

        // SalaryReportTask: Delay lần đầu 30s, sau đó lặp lại mỗi 30s
        scheduler.scheduleAtFixedRate(
                new SalaryReportTask(service),
                AppConfig.REPORT_INTERVAL,
                AppConfig.REPORT_INTERVAL,
                TimeUnit.SECONDS
        );

        System.out.printf("[Main] Đã khởi động tác vụ ngầm (AutoSave: %ds, Report: %ds).%n",
                AppConfig.AUTO_SAVE_INTERVAL, AppConfig.REPORT_INTERVAL);

        //SHUTDOWN HOOK
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[Main] Đang tiến hành dọn dẹp và tắt ứng dụng...");

            // Dừng nhận task mới, chờ task đang chạy hoàn thành (tối đa 5s)
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                    System.out.println("  -> Buộc dừng ThreadPool do quá thời gian chờ.");
                } else {
                    System.out.println("  -> ThreadPool đã dừng gọn gàng.");
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // Lưu dữ liệu lần cuối
            try {
                System.out.println("  -> Đang lưu dữ liệu lần cuối...");
                repository.saveData();
            } catch (Exception e) {
                System.err.println("  [!] Lỗi lưu dữ liệu khi tắt: " + e.getMessage());
            }

            System.out.println("[Main] Hệ thống đã tắt hoàn toàn. Tạm biệt!");
        }, "shutdown-hook"));

        // KHỞI ĐỘNG UI
        ConsoleMenu menu = new ConsoleMenu(service);
        menu.start();


        System.exit(0);
    }
}