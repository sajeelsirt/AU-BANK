package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.util.HttpCalling;
import aubank.retail.liabilities.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static aubank.retail.liabilities.util.ApplicationConstant.SERVICE;


/**
 * Service :   Service layer  to consume any service
 *
 * @author Himanshi Manchanda
 */


@Service
public class ServiceExecuteImpl {

    @Autowired
    Utility utility;


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    HttpCalling httpCalling;

   /* public Object executeService(HttpHeaders httpHeaders, Object requestMap) throws JsonProcessingException, VideoBoxException {

        VahanaConnector connector = vahanaFactory.getConnector(Api.SERVICE);
        Object o = connector.performVahanaIntegration(null, httpHeaders.getFirst("servicename"), httpHeaders, requestMap.toString());
        return o;
    }*/

    public Object executeService(HttpHeaders httpHeaders, Object requestMap) throws JsonProcessingException, AuRlMicroServiceException {
        //utility.checkHeadersService(httpHeaders);

        return httpCalling.callExecuteApi(httpHeaders, requestMap, SERVICE);

    }

    public JsonNode parseESBResponse(Object response, String apiName) throws IOException {
        JsonNode responseObj = objectMapper.readTree(String.valueOf(response));

        if (responseObj.get(apiName).has("records")) {
            boolean serviceStatus = responseObj.get(apiName).get("records").get(0).has("data");
            JsonNode serviceResponse = serviceStatus ? responseObj.get(apiName).get("records").get(0).get("data").get(0) : responseObj.get(apiName).get("records").get(0).get("error");

            return serviceResponse;
        } else {

            return null;
        }

    }


}
