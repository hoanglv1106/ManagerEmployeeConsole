package exception;
//Trùng ID khi thêm mới
public class DuplicateIdExeption extends RuntimeException {
    public DuplicateIdExeption(String message) {
        super(message);
    }
}
