package au.revamp.journey.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuRevampJourneyException extends Exception {
    private String statusCode;
    private String message;
    private transient Object errorMessages;


    public AuRevampJourneyException(String errorCode, String message) {
        this.statusCode = errorCode;
        this.message = message;
    }
}
