package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EKycDto {
    private String uid;
    private PoiDto poi;

}