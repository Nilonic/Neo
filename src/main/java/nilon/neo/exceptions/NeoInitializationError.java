package nilon.neo.exceptions;

public class NeoInitializationError extends RuntimeException {
    public NeoInitializationError(String message) {
        super(message);
    }

    public NeoInitializationError(String message, Throwable cause) {
        super(message, cause);
    }

    public NeoInitializationError(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return "NeoInitializationError: " + getMessage();
    }
}
