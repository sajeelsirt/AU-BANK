package aubank.retail.liabilities.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuRlMicroServiceException extends Exception {

    private String message;
    private transient Object errorMessages;


    public AuRlMicroServiceException(String message) {
        this.message = message;
    }

    public AuRlMicroServiceException(String errorCode, String message,String product) {

        this.message = message+" "+product;
    }
}
