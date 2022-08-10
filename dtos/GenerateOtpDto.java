package aubank.retail.liabilities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateOtpDto {

    @NotBlank(message = "Mobile Number should not be blank")
    @Length(max = 10)
    @JsonProperty(value = "MOBILE_NUMBER")
    private String MOBILE_NUMBER;

    @JsonProperty(value = "PRODUCT_CODE")
    private String PRODUCT_CODE;
}
