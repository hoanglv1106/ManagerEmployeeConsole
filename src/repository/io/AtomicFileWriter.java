package repository.io;

import config.AppConfig;
import config.FileConfig;
import model.Employee; // Đã sửa tên package viết thường

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AtomicFileWriter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AppConfig.DATE_FORMAT_PATTERN);

    public void write(List<Employee> employees) throws IOException {
        Path filePath = Paths.get(FileConfig.FILE_PATH);
        Path bakPath  = Paths.get(FileConfig.BAK_PATH);
        Path tmpPath  = Paths.get(FileConfig.TMP_PATH);

        try {
            // 1. Đảm bảo thư mục (VD: data/) đã tồn tại trước khi ghi
            if (tmpPath.getParent() != null) {
                Files.createDirectories(tmpPath.getParent());
            }

            // 2. Ghi dữ liệu ra file .tmp
            try (BufferedWriter bw = Files.newBufferedWriter(tmpPath)) {
                bw.write("id,name,email,phone,salary,hireDate,department,type,status");
                bw.newLine();

                for (Employee emp : employees) {
                    bw.write(toCsvLine(emp));
                    bw.newLine();
                }
            }

            // 3. Backup file gốc .csv -> .bak (nếu file gốc tồn tại)
            if (Files.exists(filePath)) {
                Files.copy(filePath, bakPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 4. Đổi tên .tmp -> .csv
            Files.move(tmpPath, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            // ROLLBACK: Xóa rác nếu quá trình ghi/move gặp lỗi
            try {
                Files.deleteIfExists(tmpPath);
            } catch (IOException ignored) {
                // Lỗi khi xóa file tạm thì cứ bỏ qua để ném lỗi chính ra ngoài
            }

            throw e;
        }
    }

    private String toCsvLine(Employee emp) {
        return String.join(",",
                emp.getId(),
                emp.getName(),
                emp.getEmail(),
                emp.getPhone(),
                String.valueOf(emp.getSalary()),
                emp.getHireDate().format(DATE_FORMATTER),
                emp.getDepartment().name(),
                emp.getType().name(),
                emp.getStatus().name()
        );
    }
}