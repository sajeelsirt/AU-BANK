package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class AadhaarObject implements Serializable {

        private static final long serialVersionUID = 9644474993254151L;
        private String aofId;
        private String status;
        private String doneOn;
        private String doneBy;
        private EKycDto ekycData;
        private PoaDto poaDto;
        private LdataDto ldata;
        private String pht;
        private String prn;
        private String signature;
}
