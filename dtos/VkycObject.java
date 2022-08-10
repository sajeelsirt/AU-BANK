package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
public class VkycObject implements Serializable {

    private static final long serialVersionUID = 9644474993254151L;

    //data added for testing only need to remove later
    private String data;

}
