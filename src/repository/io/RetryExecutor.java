package repository.io;

import config.FileConfig;
import exception.DataCorruptionException;
public class RetryExecutor {

    /**
     * @param action: khooi lenh can thuc thi (co the nem ngoai le)
     * @param actionName: ten cua action (de log)
     * @thows DataCorruptionException: neu da retry het so lan ma van that bai, thi nem ra ngoai le nay de thong bao cho nguoi dung biet rang da co su co xay ra va can kiem tra lai file
     *
     */
    public static void execute(RunnableWithException action,String actionName) {
        Exception lastException = null;
        for (int attempt = 1; attempt <= FileConfig.MAX_RETRIES + 1; attempt++) {
            try {
                action.run();
                return; // neu thanh cong thi thoat khoi ham
            } catch (Exception e) {
                lastException = e;
                System.err.printf("Chạy tác vụ '%s' thất bại (lần %d/%d): %s%n ",
                        actionName, attempt, FileConfig.MAX_RETRIES + 1, e.getMessage());
                if (attempt <= FileConfig.MAX_RETRIES) {
                    try {
                        Thread.sleep(500L * attempt); // tang thoi gian cho lan retry tiep theo
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // dat lai trang thai interrupt
                        System.err.println("Tác vụ bị gián đoạn trong quá trình retry.");
                        break; // thoat khoi vong lap retry
                    }
                }
            }
        }
    }
    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Exception;
    }
}
