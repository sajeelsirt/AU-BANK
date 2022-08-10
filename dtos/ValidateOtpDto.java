package aubank.retail.liabilities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidateOtpDto {
    @JsonProperty(value = "ID")
    private String ID;

    @JsonProperty(value = "OTP")
    private String OTP;

    @JsonProperty(value = "PRODUCT")
    private String Product;
}
