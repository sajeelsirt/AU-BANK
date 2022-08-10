package aubank.retail.liabilities.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {

    private String status;
    private String message;
    private int statusCode;
    private Object response;

    public ResponseDto(String status, int statusCode , String message, Object response)
    {
        this.status = status;
        this.message = message;
        this.response = response;
        this.statusCode = statusCode;
    }
    private String nextScreen;
}


