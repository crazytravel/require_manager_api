package cc.iteck.rm.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = -6328400989115027599L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        cause.printStackTrace();
    }
}
