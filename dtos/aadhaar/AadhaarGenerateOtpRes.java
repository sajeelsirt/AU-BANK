package aubank.retail.liabilities.dtos.aadhaar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AadhaarGenerateOtpRes {

    @JsonProperty(value = "response_data")
    private AadhaarResponseData aadhaarResponseData;

    @JsonProperty(value = "response_status")
    private AadhaarResponseStatus aadhaarResponseStatus;

}
