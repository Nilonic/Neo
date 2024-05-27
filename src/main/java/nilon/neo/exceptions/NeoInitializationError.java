package nilon.neo.exceptions;

public class NeoInitializationError extends RuntimeException {
    public NeoInitializationError(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "NeoInitializationError: " + getMessage();
    }
}
