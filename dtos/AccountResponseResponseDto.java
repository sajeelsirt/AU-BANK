package aubank.retail.liabilities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AccountResponseResponseDto {
    private String status;
    private String flag;
    private String accountNumber;
    @JsonProperty(value = "CRN")
    private String crn;
    private String message;
}
