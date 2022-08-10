package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class IncomeDto {
    private String agriculture;
    private String nonAgriculture;
    private Integer annual;
}
