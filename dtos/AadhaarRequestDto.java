package aubank.retail.liabilities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AadhaarRequestDto {

    @JsonProperty(value = "AADHAAR")
    private String AADHAAR;

    @JsonProperty(value = "AOF_ID")
    private String AOF_ID;

    @JsonProperty(value = "TOKEN_ID")
    private String TOKEN_ID;

    @JsonProperty(value = "PID")
    private String PID;

    @JsonProperty(value = "HMAC")
    private String HMAC;

    @JsonProperty(value = "CI")
    private String CI;

    @JsonProperty(value = "SKEY_VALUE")
    private String SKEY_VALUE;

    @JsonProperty(value = "DP_ID")
    private String DP_ID;

    @JsonProperty(value = "RDS_ID")
    private String RDS_ID;

    @JsonProperty(value = "RDS_VER")
    private String RDS_VER;

    @JsonProperty(value = "DC")
    private String DC;

    @JsonProperty(value = "MC")
    private String MC;

    @JsonProperty(value = "MI")
    private String MI;

    @JsonProperty(value = "MOBILE_NUMBER")
    private String MOBILE_NUMBER;


    @JsonProperty(value = "USER_HANDLE_TYPE")
    private String USER_HANDLE_TYPE;

    @JsonProperty(value = "USER_HANDLE_VALUE")
    private String USER_HANDLE_VALUE;

    @JsonProperty(value = "FUNCTION_SUB_CODE")
    private String FUNCTION_SUB_CODE;

    @JsonProperty(value = "CHANNEL_CODE")
    private String CHANNEL_CODE;

    @JsonProperty(value = "STAN")
    private String STAN;

    @JsonProperty(value = "TRANSMISSION_DATE_TIME")
    private String TRANSMISSION_DATE_TIME;

    @JsonProperty(value = "LOCATION")
    private String LOCATION;
    @JsonProperty(value = "FUNCTION_CODE")
    private String FUNCTION_CODE;

    @JsonProperty(value = "CLIENT_REF_NUMBER")
    private String CLIENT_REF_NUMBER;

    @JsonProperty(value = "TRANSMISSION_REF_NUMBER")
    private String TRANSMISSION_REF_NUMBER;

    @JsonProperty(value = "REMARK")
    private String REMARK;

    @JsonProperty(value = "CONSENT")
    private String CONSENT;

    @JsonProperty(value = "PURPOSE")
    private String PURPOSE;

    @JsonProperty(value = "OTP_VALUE")
    private String OTP_VALUE;

    @JsonProperty(value = "PID_TYPE")
    private String PID_TYPE;

    @JsonProperty(value = "PID_VALUE")
    private String PID_VALUE;

    @JsonProperty(value = "PDF")
    private String PDF;

    @JsonProperty(value = "LR")
    private String LR;

    @JsonProperty(value = "PI")
    private String PI;

    @JsonProperty(value = "PA")
    private String PA;

    @JsonProperty(value = "PFA")
    private String PFA;

    @JsonProperty(value = "BIO")
    private String BIO;

    @JsonProperty(value = "BT")
    private String BT;

    @JsonProperty(value = "OTP")
    private String OTP;

    @JsonProperty(value = "NOTIFICATION_CHANNEL")
    private String NOTIFICATION_CHANNEL;

    @JsonProperty(value = "CHANNEL")
    private String CHANNEL;

    @JsonProperty(value = "REQUEST_ID")
    private String REQUEST_ID;

}
