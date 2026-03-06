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
    public static void execute(RunnableWithException action, String actionName) throws DataCorruptionException {
        Exception lastException = null;

        for (int attempt = 1; attempt <= FileConfig.MAX_RETRIES + 1; attempt++) {
            try {
                action.run();
                return; // thành công → thoát
            } catch (Exception e) {
                lastException = e;
                System.err.printf("[RetryExecutor] Tác vụ '%s' thất bại (lần %d/%d): %s%n",
                        actionName, attempt, FileConfig.MAX_RETRIES + 1, e.getMessage());

                if (attempt <= FileConfig.MAX_RETRIES) {
                    try {
                        Thread.sleep(500L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        System.err.println("[RetryExecutor] Bị gián đoạn trong retry.");
                        break;
                    }
                }

            }
        }

        // Throw SAU KHI đã hết tất cả lần thử
        throw new DataCorruptionException(
                "Tác vụ '" + actionName + "' thất bại sau " + (FileConfig.MAX_RETRIES + 1)
                        + " lần thử: " + lastException.getMessage()
        );
    }

    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Exception;
    }
}
