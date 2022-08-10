package aubank.retail.liabilities.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class ContactObject implements Serializable {

    private static final long serialVersionUID = 9644474993254151L;
    private String aofId;
    private String title;
    private String fName;
    private String mName;
    private String lName;
    private String fullName;
    private String motherMaidenName;
    private String fatherName;
    private String shortName;
    private String dob;
    private String panApplied;
    private String panAppliedDate;
    private String politicallyExposed;
    private String nps;
    private String occupation;
    private String designation;
    private String designationOther;
    private String qualification;
    private String prefLang;
    private String employmentStatus;
    private String cifId;
    private String lastKycDate;
    private String riskProfile;
    private MobileDto mobile;
    private EmailDto email;
    private GenderDto gender;
    private MaritalStatusDto maritalStatus;
    private IncomeDto income;
}
