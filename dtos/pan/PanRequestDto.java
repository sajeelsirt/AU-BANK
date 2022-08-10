package aubank.retail.liabilities.dtos.pan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PanRequestDto {
    @JsonProperty(value = "PAN_NUMBER")
    private String panId;

    @JsonProperty(value = "AOF_ID")
    private String aofId;
}
