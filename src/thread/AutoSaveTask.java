package thread;


import exception.DataCorruptionException;
import repository.EmployeeRepository;

public class AutoSaveTask implements Runnable {

    private final EmployeeRepository repository;

    public AutoSaveTask(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run() {
        try {
            repository.saveData();
            System.out.println("[AutoSaveTask] Auto-save thành công lúc: " + java.time.LocalTime.now().withNano(0));
        } catch (DataCorruptionException e) {
            System.err.println("[AutoSaveTask] Lỗi nghiêm trọng khi auto-save: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[AutoSaveTask] Lỗi không xác định: " + e.getMessage());
        }
    }
}
