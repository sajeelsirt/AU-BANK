package aubank.retail.liabilities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class AccountObject implements Serializable {

    private static final long serialVersionUID = 9644474993254151L;
    @JsonProperty(value = "ACCOUNT_NUMBER")
    private String accountNumber;
    @JsonProperty(value = "CIF_NUMBER")
    private String cifNumber;
    @JsonProperty(value = "ACCOUNT_STATUS")
    private String accountStatus;
    @JsonProperty(value = "ACCOUNT_API_STATUS")
    private String accountApiStatus;
    @JsonProperty(value = "ACCOUNT_RESPONSE")
    private AccountResponseDto accountResponse;

}
