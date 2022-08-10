package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MobileDto {
    private String status;
    private String mobileNo;
    private String countryCode;
    private String verifiedVia;
}
