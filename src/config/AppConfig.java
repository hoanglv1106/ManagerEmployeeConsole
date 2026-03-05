package config;

import java.time.format.DateTimeFormatter;

public class AppConfig {
    public static final int AUTO_SAVE_INTERVAL = 60;   // seconds
    public static final int REPORT_INTERVAL    = 30;   // seconds
    public static final int THREAD_POOL_SIZE   = 2;
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private AppConfig() {}
}
