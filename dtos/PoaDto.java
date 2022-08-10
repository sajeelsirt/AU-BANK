package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PoaDto {
    private String co;
    private String house;
    private String street;
    private String lm;
    private String loc;
    private String vtc;
    private String subdist;
    private String dist;
    private String state;
    private String country;
    private String pc;
    private String po;
}
