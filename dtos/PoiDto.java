package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PoiDto {
    private String name;
    private String dob;
    private String gender;
    private String phone;
    private String email;
}
