package util;

import config.AppConfig;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtil {

    private static final Scanner SCANNER = new Scanner(System.in);


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AppConfig.DATE_FORMAT_PATTERN);

    private InputUtil() {}

    public static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("   Đầu vào không hợp lệ. Vui lòng nhập số nguyên.");
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.printf("   Vui lòng nhập số trong khoảng từ %d đến %d.%n", min, max);
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("   Đầu vào không hợp lệ. Vui lòng nhập một con số.");
            }
        }
    }

    public static double readPositiveDouble(String prompt) {
        while (true) {
            double value = readDouble(prompt);
            if (value >= 0) {
                return value;
            }
            System.out.println("   Giá trị không được là số âm.");
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {

                System.out.printf("   Sai định dạng ngày. Vui lòng nhập theo định dạng: %s%n", AppConfig.DATE_FORMAT_PATTERN);
            }
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            String input = readString(prompt + " (y/n): ").toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no"))  return false;
            System.out.println("   Vui lòng chỉ nhập y (có) hoặc n (không).");
        }
    }

    public static <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass) {
        while (true) {
            // Tự động chuyển input thành chữ hoa để match với tên Enum
            String input = readString(prompt).toUpperCase();
            try {
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("  Lựa chọn không hợp lệ. Vui lòng nhập đúng tên trong ngoặc vuông.");
            }
        }
    }
}