package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class CkycObject implements Serializable {

    private static final long serialVersionUID = 9644474993254151L;

    //data added for testing only need to remove later
    private String data;

}
