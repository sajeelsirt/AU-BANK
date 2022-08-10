package aubank.retail.liabilities.service.aadhaar;


import aubank.retail.liabilities.dtos.AadhaarRequestDto;
import aubank.retail.liabilities.dtos.OnlineProcessResponse;
import aubank.retail.liabilities.service.impl.IgwExecuteImpl;
import aubank.retail.liabilities.util.EventData;
import aubank.retail.liabilities.util.HttpCalling;
import aubank.retail.liabilities.util.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.java.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Log
public class ValidateAadharOtp {

    @Autowired
    CommonUtility commonUtility;

    @Autowired
    HttpCalling igwExecute;
    @Autowired
    IgwExecuteImpl igwExecuteImpl;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ResponseParserValidate responseParserValidate;

    @Autowired
    Utility utility;

    public Object validateAadharOtp(HttpHeaders httpHeaders, AadhaarRequestDto auRequestDto) {
        Map<String, Object> serverJsonRequest = createServerJsonRequest(auRequestDto, httpHeaders);
       // String serverJsonRequest = createServerJsonRequest(auRequestDto, httpHeaders);
        Map<String, String> stringStringMap = performPostRequestAndParseResponse(httpHeaders, auRequestDto, serverJsonRequest);
        return stringStringMap;
    }


    public Map<String, String> performPostRequestAndParseResponse(HttpHeaders httpHeaders, AadhaarRequestDto auRequestDto, Map<String, Object> serverRequest) {
        String sdkNewUrl = httpHeaders.getFirst("AADHAAR_SERVICE_OTP_URL");
        OnlineProcessResponse finalResponse = new OnlineProcessResponse();
        Map<String, String> ekycResponseMap = new HashMap<>();
        String responseCode = "";
        String responseBody = "";
        String clientResponseMessage = "";
        String code = "";
        String message = "";
        String status = "";
        String finalProcessStatus = "";
        ResponseEntity<String> response = null;
        EventData eventData = new EventData();
        try {
            String url = sdkNewUrl;
            log.info("URL : " + url);

            log.info("AADHAR EKYC REQUEST : ======= " + serverRequest);

//            String hash = httpheader(serverRequest, httpHeaders);

//            HttpHeaders header = new HttpHeaders();
//            header.add("Content-Hash", hash);
//            header.add("content-Type", "application/json");
//            header.add("accept", "application/json");

            HttpHeaders header = utility.getServiceHttpHeaders("GENERATE_AADHAAR_OTP",
                    auRequestDto.getAOF_ID());

       //     eventData.setRequest(objectMapper.readTree(serverRequest));
            eventData.setHeaders(header.toSingleValueMap());

           // response = igwExecute.sendPostRequest(url, serverRequest, header);
            String responseMethod = igwExecuteImpl.executeIgwApi(header,serverRequest);
            response = objectMapper.convertValue(responseMethod, ResponseEntity.class);

            log.info("RESPONSE ================== " + response);
            responseCode = response.getStatusCode().toString();
            log.info("responseCode : " + responseCode);
            responseBody = (String) response.getBody();
            log.info("responseBody================= " + responseBody);

            JsonObject respObj = new JsonParser().parse(responseBody)
                    .getAsJsonObject();

            JsonObject responsestatus = respObj.getAsJsonObject()
                    .get("response_status").getAsJsonObject();

            log.info("Response status is=================: " + responsestatus);

            code = responsestatus.get("code").getAsString();
            message = responsestatus.get("message").getAsString();
            status = responsestatus.get("status").getAsString();

            log.info("Responsecode:  " + code + "ResponseMessage is : "
                    + message + "responsestatus is : " + status);

            if (code.equalsIgnoreCase("000")) {
                JSONObject decresp = getDecryptResponse(responseBody, httpHeaders, auRequestDto);

                log.info("SUCCESS CASE");
                finalProcessStatus = "SUCCESS";
                finalResponse.setProcessStatus("SUCCESS");
                finalResponse.setResponseType("I");
                finalResponse.setMessageCode(code);
                finalResponse.setResponseMessage(message);
                finalResponse.setResponse(
                        decresp.getJSONObject("response_data").toString());

                eventData.setResponse(decresp);

            } else {
                log.info("FAILURE CASE");
                finalProcessStatus = "FAILURE";
                finalResponse.setProcessStatus("FAILURE");
                finalResponse.setResponseType("E");
                finalResponse.setMessageCode(code);
                finalResponse.setResponseMessage(message);

                eventData.setResponse(message);
            }

        } catch (Exception e) {
            code = "000006";
            message = "Some error occured in connecting to the aadhaar service";
            finalProcessStatus = "FAILURE";
            responseBody = response.toString();

            finalResponse.setProcessStatus(finalProcessStatus);
            finalResponse.setMessageCode(code);
            finalResponse.setResponseMessage(e.getMessage());
            finalResponse.setResponseType("E");
            ekycResponseMap.put("error", new Gson().toJson(finalResponse));
            e.printStackTrace();

        }
        clientResponseMessage = new Gson().toJson(finalResponse);

        ekycResponseMap.put("processRequestId", httpHeaders.getFirst("servicename"));
        ekycResponseMap.put("responseCode", responseCode);
        ekycResponseMap.put("responseSubCode", code);
        ekycResponseMap.put("responseMessage", message);
        ekycResponseMap.put("refOutData1", "NA");
        ekycResponseMap.put("refOutData2", "NA");
        ekycResponseMap.put("serverResponse", responseBody);
        ekycResponseMap.put("clientResponse", clientResponseMessage);
        ekycResponseMap.put("finalProcessStatus", finalProcessStatus);
        ekycResponseMap.put("ekycTimeStamp", LocalDateTime.now().toString());


        log.info("CLIENT response===>" + ekycResponseMap);

        //  commonUtility.storeAadharData(auRequestDto.getVT_ID(), "",eventData,"","","","VALIDATE_AADHAAR_OTP");
        return ekycResponseMap;
    }

    public Map<String, Object> createServerJsonRequest(AadhaarRequestDto auRequestDto, HttpHeaders httpHeaders) {
      //  String serverRequest = null;
        String environmentProp = httpHeaders.getFirst("CLIENT_ENVIRONMENT");
        Map<String, Object> serverRequest = new HashMap<>();

        JsonObject headers = new JsonObject();
        try {
            HashMap<String, String> hashMap = commonUtility.newaadhaar(auRequestDto.getOTP_VALUE(), httpHeaders);

            serverRequest.put("TRANSMISSION_DATETIME", String.valueOf(Instant.now().getEpochSecond()));
            serverRequest.put("USER_HANDLE_TYPE", auRequestDto.getUSER_HANDLE_TYPE());
            serverRequest.put("USER_HANDLE_VALUE", auRequestDto.getUSER_HANDLE_VALUE());
            serverRequest.put("CHANNEL_REF_NUMBER", auRequestDto.getTRANSMISSION_REF_NUMBER());
            serverRequest.put("CLIENT_CODE","AUBANK121");
            serverRequest.put("SUB_CLIENT_CODE", "AUBANK121");
            serverRequest.put("CHANNEL_CODE", auRequestDto.getCHANNEL_CODE());
            serverRequest.put("client_ip", "");
            serverRequest.put("operation_mode", "ASSISTED");
            serverRequest.put("run_mode", "REAL");
            serverRequest.put("actor_type", "CUSTOMER");

            serverRequest.put("location", "");
            serverRequest.put("function_code", "EKYC");
            serverRequest.put("function_sub_code", "DEFAULT");
            serverRequest.put("client_ref_number", auRequestDto.getCLIENT_REF_NUMBER());
            serverRequest.put("environment", environmentProp);
            serverRequest.put("notification_channel", auRequestDto.getNOTIFICATION_CHANNEL());

            log.info("Server Request is : " + serverRequest);
//            headers.addProperty("channel", auRequestDto.getCHANNEL());
//            headers.addProperty("requestId", auRequestDto.getREQUEST_ID());
//            headers.addProperty("client_code", "AUBANK121");
//            headers.addProperty("sub_client_code", "AUBANK121");
//            headers.addProperty("channel_code", auRequestDto.getCHANNEL_CODE());
//            headers.addProperty("channel_version", "0.0.1");
//            headers.addProperty("stan", auRequestDto.getSTAN());
            //headers.addProperty("stan", String.valueOf(stan));

//            headers.addProperty("client_ip", "");
//            headers.addProperty("transmission_datetime", String.valueOf(Instant.now().getEpochSecond()));
//            headers.addProperty("operation_mode", "ASSISTED");
//            headers.addProperty("run_mode", "REAL");
//            headers.addProperty("actor_type", "CUSTOMER");
//            headers.addProperty("user_handle_type", auRequestDto.getUSER_HANDLE_TYPE());
//            headers.addProperty("user_handle_value", auRequestDto.getAADHAAR());

//            headers.addProperty("location", "");
//            headers.addProperty("function_code", "EKYC");
//            headers.addProperty("function_sub_code", "DEFAULT");

//            JsonObject request = new JsonObject();
//            request.addProperty("client_ref_number", auRequestDto.getCLIENT_REF_NUMBER());
//            request.addProperty("transaction_ref_number", auRequestDto.getTRANSMISSION_REF_NUMBER());
//            request.addProperty("remark", auRequestDto.getREMARK());
//            request.addProperty("environment", environmentProp);
//            request.addProperty("notification_channel", auRequestDto.getNOTIFICATION_CHANNEL());

            JsonObject pid = new JsonObject();
            pid.addProperty("type", auRequestDto.getPID_TYPE());
            pid.addProperty("value", (String) hashMap.get("PID"));
            pid.addProperty("value", (String) hashMap.get("PID"));

            serverRequest.put("pid", pid);

            serverRequest.put("hmac", (String) hashMap.get("HMAC"));
            serverRequest.put("uid", auRequestDto.getAADHAAR());
            serverRequest.put("consent", auRequestDto.getCONSENT());
            serverRequest.put("pdf", "");
            serverRequest.put("lr", "");
            serverRequest.put("purpose", auRequestDto.getPURPOSE());

            JsonObject skey = new JsonObject();
            skey.addProperty("ci", (String) hashMap.get("CI"));
            skey.addProperty("value", (String) hashMap.get("SKEY"));
            serverRequest.put("skey", skey);

            JsonObject modalities = new JsonObject();
            modalities.addProperty("pi", auRequestDto.getPI());
            modalities.addProperty("pa", auRequestDto.getPA());
            modalities.addProperty("pfa", auRequestDto.getPFA());
            modalities.addProperty("bio", auRequestDto.getBIO());
            modalities.addProperty("bt", "");
            modalities.addProperty("otp", auRequestDto.getOTP());

            serverRequest.put("modalities", modalities);

//            JsonObject finalreq = new JsonObject();
//            finalreq.add("headers", headers);
//            finalreq.add("request", request);

     //       serverRequest = new Gson().toJson(finalreq);
            String serverRequest1 = new Gson().toJson(serverRequest);
            String hash = httpheader(serverRequest1, httpHeaders);
            serverRequest.put("CONTENT_HASH", hash);
            serverRequest.put("AOF_ID", auRequestDto.getAOF_ID());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverRequest;
    }

    public String httpheader(String request, HttpHeaders httpHeaders) {
        String environment = httpHeaders.getFirst("CLIENT_ENVIRONMENT");
        String salt_id​ = "";
        String salt​ = "";
        if ("PREPROD".equalsIgnoreCase(environment)) {
            salt_id​ = "04a80a47";
            salt​ = "2fee10a8b139";

        } else {
            salt_id​ = "f06df945";
            salt​ = "3950332ac1a6";
        }

        String header​ = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt​.getBytes("UTF-8"));
            String payload = request;

            String hash​ = java.util.Base64.getEncoder()
                    .encodeToString(digest.digest(payload.getBytes("UTF-8")));
            header​ = salt_id​ + ":" + hash​;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        log.info("header is " + header​);
        return header​;
    }

    public JSONObject getDecryptResponse(String encryptedResponse, HttpHeaders httpHeaders, AadhaarRequestDto auRequestDto)
            throws JSONException {
        JSONObject finalResponse = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject(encryptedResponse);
            JSONObject json = jsonObject.getJSONObject("response_data");
            Object data = json.opt("data");
            String decoded = new String(
                    DatatypeConverter.parseBase64Binary(data.toString()));
            JSONObject js = responseParserValidate.parseResponse(decoded, auRequestDto);

            String uidtoken = jsonObject.getString("uid_token");
            log.info("uid token is " + uidtoken);
            js.put("uid_token", uidtoken);

            JSONObject responseStatus = jsonObject
                    .getJSONObject("response_status");
            finalResponse.put("response_data", js);
            finalResponse.put("response_status", responseStatus);
        } catch (Exception e) {
            finalResponse.put("Exception", e);
            e.printStackTrace();
        }
        return finalResponse;
    }

}
