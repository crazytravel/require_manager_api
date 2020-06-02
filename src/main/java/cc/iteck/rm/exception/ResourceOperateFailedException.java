package cc.iteck.rm.exception;

public class ResourceOperateFailedException extends RuntimeException {

    private static final long serialVersionUID = -9075652443083324229L;

    public ResourceOperateFailedException(String message) {
        super(message);
    }

    public ResourceOperateFailedException(String message, Throwable cause) {
        super(message, cause);
        cause.printStackTrace();
    }
}
