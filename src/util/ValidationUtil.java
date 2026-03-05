package util;

import config.AppConfig;
import exception.InvalidFormatException;
import exception.NegativeSalaryException;

import java.time.format.DateTimeFormatter;

public class ValidationUtil {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PHONE_REGEX = "^(\\+84|0)[3-9]\\d{8}$";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AppConfig.DATE_FORMAT_PATTERN);

    private ValidationUtil() {}
    public static void validateEmail(String email){
        if (email == null || !email.matches(EMAIL_REGEX)) {
            throw new InvalidFormatException("Email không đúng định dạng: " + email);

        }
    }
    public static void validatePhone(String phone){
        if (phone == null || !phone.matches(PHONE_REGEX)) {
            throw new InvalidFormatException("Số điện thoại không đúng định dạng: " + phone);
        }
    }
    public static void validateDate(String dateStr){
        try {
            DATE_FORMATTER.parse(dateStr);
        } catch (Exception e) {
            throw new InvalidFormatException("Ngày tháng năm đúng định dạng: " + dateStr + ". Expected format: yyyy-MM-dd");
        }
    }

    public static void validateSalary(double salary){
        if (salary < 0) {
            throw new NegativeSalaryException("lương không được phép la số âm : " + salary);
        }
    }
}
