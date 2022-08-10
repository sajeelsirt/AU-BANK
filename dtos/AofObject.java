package aubank.retail.liabilities.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class AofObject implements Serializable {

    private static final long serialVersionUID = 9644474993254151L;
    private String aofId;
    private String status;
    private String statusMessage;
    private PrimaryHolderDto primaryHolderDto;
    private CodeValueObjectDto codeValueObjectDto;
    private JsonNode handOff;
    private String nextScreen;
}
