package exception;
//Hết retry + rollback thất bại
public class DataCorruptionException extends RuntimeException {
    public DataCorruptionException(String message) {
        super(message);
    }
}
