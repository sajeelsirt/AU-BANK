package aubank.retail.liabilities.service.aadhaar;

import aubank.retail.liabilities.dtos.AadhaarRequestDto;
import aubank.retail.liabilities.util.HttpCalling;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;


@Service
@Log
public class AadharServiceImpl {

    @Autowired
    GetToken getToken;

    @Autowired
    HttpCalling httpCalling;

    @Value("${igw.url}")
    private String igwUrl;

    public Object aadharVault(HttpHeaders httpHeaders, AadhaarRequestDto auRequestDto) {

        try {

            String aadhaar = auRequestDto.getAADHAAR();
            String tokenid = auRequestDto.getTOKEN_ID();

            String reqid = httpHeaders.getFirst("requestid");

            log.info("IGW reqid is :" + reqid);

            if (tokenid != null && !tokenid.trim().isEmpty()) {
                // if tokenid is not empty call aadharvault igw service
                log.info("=========call only igw aadharvault; tokenId="
                        + tokenid);
                return callAadharVaultVahana(auRequestDto, httpHeaders);
            } else {
                log.info("=========call DOTOKENIZATION============");
                // call doTokenization
                tokenid = getToken.callDoTokenizationApi(httpHeaders, aadhaar, reqid);
                if (tokenid != null && !tokenid.trim().isEmpty()) {

                    auRequestDto.setTOKEN_ID(tokenid);

                    log.info("requestBody after dotokenization:" + auRequestDto);
                    log.info("==============call aadharVault=========");
                    return callAadharVaultVahana(auRequestDto, httpHeaders);

                } else {
                    return getFailureResponse();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return getFailureResponse();

        }

    }

    private Object callAadharVaultVahana(AadhaarRequestDto auRequestDto,
                                         HttpHeaders httpHeaders) throws JsonProcessingException {

        log.info("Making iGW call now...");
        String url = "";

        return httpCalling.sendPostRequest(url, new Gson().toJson(auRequestDto), httpHeaders).getBody();
    }


    private String getFailureResponse() {
        JsonObject output = new JsonObject();
        output.addProperty("response_type", "E");
        output.addProperty("response_message", "Process Failure");
        output.addProperty("response_status", "FAILURE");
        output.addProperty("referenceno", "");
        log.info("RESPONSE=" + output);
        return new Gson().toJson(output);
    }
}
