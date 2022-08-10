package aubank.retail.liabilities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AccountResponseDto {
    @JsonProperty(value = "process_status")
    private String processStatus;
    @JsonProperty(value = "response_type")
    private String responseType;
    private AccountResponseResponseDto response;
}
