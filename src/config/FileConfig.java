package config;

public class FileConfig {
    public static final String FILE_PATH = "data/employees.csv";
    public static final String BAK_PATH  = "data/employees.csv.bak";
    public static final String TMP_PATH  = "data/employees.csv.tmp";
    public static final int    MAX_RETRIES = 2;

    private FileConfig() {}
}
