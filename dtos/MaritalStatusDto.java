package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MaritalStatusDto {
    private String code;
    private String value;
    private Integer sequence;
}
