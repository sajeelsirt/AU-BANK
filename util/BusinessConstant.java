package aubank.retail.liabilities.util;

public class BusinessConstant {

    private BusinessConstant()
    {

    }
    public static final String DATE_FORMAT_V1 = "dd-MM-yyyy hh:mm:ss";
    public static final String DATE_FORMAT_V2 = "dd-MM-yyyy";
    public static final String AGENT_FAILED = "REJECTED";
    public static final String VKYC_AUDITOR_APPROVE = "APPROVED";
    public static final String VKYC_AUDITOR_REJECT = "NOTAPPROVED";
    public static final String AGENT_SUCCESS = "SUCCESSFUL";
    public static final String VKYC_UNABLE = "UNABLE";
    public static final String TIMESTAMP_COLUMN_DEF = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";

    public static final String HMAC_FAILURE_CODE = "HMAC_FAILURE";
    public static final String HMAC_FAILURE_MESSAGE = "Error occurred while doing HMAC.";

    public static final String HMAC_INVALID_CODE = "INVALID_CHECKSUM";
    public static final String HMAC_INVALID_MESSAGE = "Given Checksum is Invalid.";

}
