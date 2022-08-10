package aubank.retail.liabilities.dtos.aadhaar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AadhaarResponseStatus {

    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value = "message")
    private String message;

    @JsonProperty(value = "status")
    private String status;
}
