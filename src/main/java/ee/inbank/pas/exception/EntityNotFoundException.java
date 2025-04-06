package ee.inbank.pas.exception;

import lombok.Getter;
import java.io.Serial;

@Getter
public class EntityNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String code;

    public EntityNotFoundException(String description, String code) {
        super(description);
        this.code = code;
    }
}
