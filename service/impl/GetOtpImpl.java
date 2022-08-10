package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.alerts.OtpData;
import aubank.retail.liabilities.alerts.OtpManager;
import aubank.retail.liabilities.dtos.GenerateOtpDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GetOtpImpl {
    @Autowired
    private OtpManager otpManager;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NotificationImpl notification;


    protected void validateRequest(JsonNode request, HttpHeaders httpHeaders) {
        /*
         * Add any request param validation if required
         */
    }

    public Object executeBackendSystem(GenerateOtpDto request) throws AuRlMicroServiceException, IOException {
        // type N -> Numeric, A -> AlphaNumeric
        //Method 1
        //OtpData otpData1 = otpManager.getOtp("N", 6, 30);
        //Method 2
        OtpData otpData2 = otpManager.getOtp();
        // if you want to send otp to mobile then send via igw api

        callSmsSendApi(request, otpData2.getOtp());
        return otpData2.getId();
    }

    public String getServiceType() {
        return "GET_OTP";
    }

    private JsonNode callSmsSendApi(GenerateOtpDto generateOtpDto, String randomDigit) throws IOException, AuRlMicroServiceException {


        String alertId;

        if (generateOtpDto.getPRODUCT_CODE().equalsIgnoreCase("1")) {
            alertId = "SA_OTP";
        } else if (generateOtpDto.getPRODUCT_CODE().equalsIgnoreCase("2")) {
            alertId = "FD_OTP";
        } else if (generateOtpDto.getPRODUCT_CODE().equalsIgnoreCase("3")) {
            alertId = "SR_OTP";
        } else if (generateOtpDto.getPRODUCT_CODE().equalsIgnoreCase("5")) {
            alertId = "VKYC_OTP";
        } else if (generateOtpDto.getPRODUCT_CODE().equalsIgnoreCase("6")) {
            alertId = "MIN_TO_FKYC_MOBILE_OTP";
        } else {
            alertId = "GENERIC_OTP_VB";
        }

        ObjectNode alertContent = objectMapper.createObjectNode();
        alertContent.put("MOBILE_NO", generateOtpDto.getMOBILE_NUMBER());
        alertContent.put("OTP", randomDigit);
        return notification.raiseAlert(alertId, "", objectMapper.writeValueAsString(alertContent), "1");

    }


}
