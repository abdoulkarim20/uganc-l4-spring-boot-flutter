package gn.uganc.gestiongarage.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " non trouve(e) avec l'id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}