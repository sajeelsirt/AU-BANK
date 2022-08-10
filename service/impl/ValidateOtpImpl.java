package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.alerts.OtpManager;
import aubank.retail.liabilities.dtos.ResponseDto;
import aubank.retail.liabilities.dtos.ValidateOtpDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.util.ApplicationConstant;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class ValidateOtpImpl {
    @Autowired
    private OtpManager otpManager;

    protected void validateRequest(JsonNode request, HttpHeaders httpHeaders) {
        /*
        Add any request param validation if required
         */
    }


    public Object executeBackendSystem(ValidateOtpDto request) throws AuRlMicroServiceException {
        String id = request.getID();
        String otp = request.getOTP();
        String product = request.getProduct();
        ResponseDto responseDto = new ResponseDto();
        if (!otpManager.isOtpValid(id, otp,product)) {
            throw new AuRlMicroServiceException("Otp Invalid or Expired");

        }
            responseDto.setMessage("Otp Validated Successfully");
            responseDto.setStatus(ApplicationConstant.SUCCESS);
            responseDto.setStatusCode(0);
        return responseDto;
    }


    public String getServiceType() {
        return "VALIDATE_OTP";
    }
}
