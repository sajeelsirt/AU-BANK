package aubank.retail.liabilities.controller;

import aubank.retail.liabilities.dtos.*;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.service.aadhaar.GenerateAadharOtp;
import aubank.retail.liabilities.service.aadhaar.ValidateAadharOtp;
import aubank.retail.liabilities.service.impl.GetOtpImpl;
import aubank.retail.liabilities.service.impl.ValidateOtpImpl;
import aubank.retail.liabilities.util.ApplicationConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Log
@CrossOrigin
@RestController
@RequestMapping("aadhaar")
public class AadhaarController {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    GenerateAadharOtp generateAadharOtp;
    @Autowired
    ValidateAadharOtp validateAadharOtp;

    @PostMapping("otp/validate")
    public ResponseDto validateAadharOtp(@Valid @RequestBody AadhaarRequestDto aadhaarRequestDto, @RequestHeader HttpHeaders headers) throws AuRlMicroServiceException {
        Object o = validateAadharOtp.validateAadharOtp(headers,aadhaarRequestDto);
        ResponseDto responseDto=new ResponseDto();
        responseDto.setStatus(ApplicationConstant.SUCCESS);
        responseDto.setResponse(o);
        responseDto.setMessage("Otp Validated Successfully");
        responseDto.setStatusCode(0);
        return responseDto;
    }

    @PostMapping("otp/generate")
    public ResponseDto generateAadharOtp(@Valid @RequestBody AadhaarRequestDto aadhaarRequestDto, @RequestHeader HttpHeaders headers) throws IOException, AuRlMicroServiceException {
        // Factory Pattern
        ResponseDto responseDto=new ResponseDto();
        try {
            OnlineProcessResponse obj = generateAadharOtp.generateAadharOtp(headers, aadhaarRequestDto);
            responseDto.setResponse(obj);
            responseDto.setStatus(ApplicationConstant.SUCCESS);
            responseDto.setMessage("Otp Generated Successfully");
            responseDto.setStatusCode(0);
        }catch (Exception e){
            e.printStackTrace();
            responseDto.setStatus(ApplicationConstant.FAILURE);
            responseDto.setStatusCode(0);
        }
        return responseDto;
    }
}