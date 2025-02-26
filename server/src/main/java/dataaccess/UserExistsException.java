package dataaccess;

public class UserExistsException extends DataAccessException {
    public UserExistsException(String message) {
        super(message);
    }
}
