package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EmailDto {
    private String status;
    private String email;
    private String verifiedVia;
}
