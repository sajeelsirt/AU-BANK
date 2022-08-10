package aubank.retail.liabilities.controller;

import aubank.retail.liabilities.dtos.GenerateOtpDto;
import aubank.retail.liabilities.dtos.ResponseDto;
import aubank.retail.liabilities.dtos.ValidateOtpDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.service.impl.GetOtpImpl;
import aubank.retail.liabilities.service.impl.ValidateOtpImpl;
import aubank.retail.liabilities.util.ApplicationConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.io.IOException;

@Log
@CrossOrigin
@RestController
public class OtpController {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    GetOtpImpl getOtp;
    @Autowired
    ValidateOtpImpl validateOtp;

    // Below api we can configure as v-Connect PassThrough Service with API Category Transactional
    @PostMapping("otp/validate")
    public ResponseDto otpValidate(@Valid @RequestBody ValidateOtpDto validateOtpDto, @RequestHeader HttpHeaders headers) throws AuRlMicroServiceException {
        Object o = validateOtp.executeBackendSystem(validateOtpDto);
        ResponseDto responseDto=new ResponseDto();
        responseDto.setStatus(ApplicationConstant.SUCCESS);
        responseDto.setResponse(o);
        responseDto.setMessage("Otp Validated Successfully");
        responseDto.setStatusCode(0);
        return responseDto;
    }

    // Below api we can configure as v-Connect PassThrough Service with API Category Transactional
    @PostMapping("otp/generate")
    public ResponseDto otpGenerate(@Valid @RequestBody GenerateOtpDto generateOtpDto, @RequestHeader HttpHeaders headers) throws IOException, AuRlMicroServiceException {
        // Factory Pattern
        Object o = getOtp.executeBackendSystem(generateOtpDto);
        ResponseDto responseDto=new ResponseDto();
        responseDto.setStatus(ApplicationConstant.SUCCESS);
        responseDto.setResponse(o);
        responseDto.setMessage("Otp Generated Successfully");
        responseDto.setStatusCode(0);
        return responseDto;
    }
}