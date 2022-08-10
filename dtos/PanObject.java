package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class PanObject implements Serializable {

    private static final long serialVersionUID = 9644474993254151L;
    private String aofId;
    private String status;
    private PanDataDto panData;
}
