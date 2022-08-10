package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ConsentPanDto {
    private String status;
    private String consentId;
}
