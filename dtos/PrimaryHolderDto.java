package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PrimaryHolderDto {
    private String id;
    private String name;
    private String mobile;
    private String aml;
    private String mobileVerified;
    private String panVerified;
    private String aadhaarVerified;
    private String kycSelected;
    private String form60;
    private ConsentPanDto consentPan;
    private ConsentEKycDto consentEKyc;
    private ConsentVkycDto consentVKyc;
}
