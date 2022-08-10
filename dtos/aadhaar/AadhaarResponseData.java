package aubank.retail.liabilities.dtos.aadhaar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AadhaarResponseData {

    @JsonProperty(value = "data")
    private String data;

    @JsonProperty(value = "format")
    private String format;
}
