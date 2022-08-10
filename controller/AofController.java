package aubank.retail.liabilities.controller;

import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.dtos.ResponseDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.service.impl.AofResolver;
import aubank.retail.liabilities.util.NextScreenScheduling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("aof")
@Slf4j
public class AofController {

    @Autowired
    AofResolver aofResolver;
    @Autowired
    NextScreenScheduling nextScreenScheduling;

    @PostMapping(value = {"/get/insert"}, consumes = { MediaType.APPLICATION_JSON_VALUE }
            , produces = { MediaType.APPLICATION_JSON_VALUE })
    public Object getOrInsertAof(@RequestBody AofRequestDto requestDto, @RequestHeader HttpHeaders httpHeaders) throws IOException, AuRlMicroServiceException {
        ResponseDto responseDto = new ResponseDto();
        try{
            log.info("In AofController -> getOrinsertAof -> RequestDto ", requestDto);
            Object aofObject = aofResolver.getOrInsert(requestDto);
            responseDto.setStatus("Success");
            responseDto.setStatusCode(0);
            responseDto.setMessage("Operation has successfully performed");
            responseDto.setResponse(aofObject);
        }
        catch (Exception e){
            throw new AuRlMicroServiceException("You enter incorrect Detail Please Check");
        }

        log.info("ResponseDto from controller is : "+responseDto);
        return responseDto;
    }

    @GetMapping("/update")
    public Object updateAof(@RequestBody AofRequestDto requestDto, @RequestHeader HttpHeaders httpHeaders) throws AuRlMicroServiceException, IOException {
        ResponseDto responseDto = new ResponseDto();
        try{
            log.info("In AofController -> updateAof -> RequestDto ", requestDto);
            Object aofObject = aofResolver.update(requestDto);
            responseDto.setStatus("Success");
            responseDto.setStatusCode(0);
            responseDto.setMessage("Operation has successfully performed");
            responseDto.setResponse(aofObject);
        }
        catch (Exception e){
            throw new AuRlMicroServiceException("You enter incorrect Detail Please Check");
        }

        log.info("ResponseDto from controller is : "+responseDto);
        return responseDto;
    }
}
