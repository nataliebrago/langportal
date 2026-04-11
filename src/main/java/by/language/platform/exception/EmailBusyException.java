package by.language.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)   // 409 Conflict
public class EmailBusyException extends RuntimeException {

    public EmailBusyException(String email) {
        super("Email '" + email + "' уже заргестрирован");
    }
}