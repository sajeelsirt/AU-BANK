package aubank.retail.liabilities.controller;

import aubank.retail.liabilities.dtos.ResponseDto;
import aubank.retail.liabilities.dtos.pan.PanRequestDto;
import aubank.retail.liabilities.service.impl.PanResolver;
import aubank.retail.liabilities.util.NextScreenScheduling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pan")
@Slf4j
public class PanController {

    @Autowired
    PanResolver panResolver;

    @Autowired
    NextScreenScheduling nextScreenScheduling;

    @PostMapping("/panVerification")

    public Object panVerification(@RequestBody PanRequestDto panRequestDto, @RequestHeader HttpHeaders httpHeaders) throws Exception {
        log.info("PanRequestDto=== {} {}", panRequestDto.getPanId(), panRequestDto.getAofId());
        ResponseDto responseDto = new ResponseDto();
        try {
            Object panValidationObject = panResolver.panValidation(panRequestDto, httpHeaders);

            responseDto.setStatus("Success");
            responseDto.setStatusCode(0);
            responseDto.setMessage("Operation has successfully performed");
            responseDto.setResponse(panValidationObject);
            responseDto.setNextScreen(nextScreenScheduling.getNextScreen("Pan"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failure,Something went wrong");
        }
        return responseDto;
    }

}
