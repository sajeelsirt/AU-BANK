package aubank.retail.liabilities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AofRequestDto {
    private String loginId;
    private String aofId;
    private String objectType;
    private Object objectData;
    @JsonProperty(value = "MOBILE_NUMBER")
    private String recordIdentifier2;
    @JsonProperty(value = "PRODUCT_CODE")
    private String recordIdentifier3;
}
