package aubank.retail.liabilities.service.aadhaar;


import aubank.retail.liabilities.dtos.AadhaarRequestDto;
import aubank.retail.liabilities.dtos.OnlineProcessResponse;
import aubank.retail.liabilities.dtos.aadhaar.AadhaarGenerateOtpRes;
import aubank.retail.liabilities.service.impl.IgwExecuteImpl;
import aubank.retail.liabilities.service.impl.ServiceExecuteImpl;
import aubank.retail.liabilities.util.ApplicationConstant;
import aubank.retail.liabilities.util.HttpCalling;
import aubank.retail.liabilities.util.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GenerateAadharOtp {


//    @Value("${Environment}")
//    public String environmentProp;

//    @Value("${SDK_NEW_URL}")
//    private String sdkNewUrl;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CommonUtility commonUtility;
    @Autowired
    Utility utility;

    @Autowired
    HttpCalling igwExecute;
    @Autowired
    IgwExecuteImpl igwExecuteImpl;
    @Autowired
    ServiceExecuteImpl serviceExecute;

    public OnlineProcessResponse generateAadharOtp(HttpHeaders httpHeaders, AadhaarRequestDto auRequestDto) {
        Map<String, Object> serverJsonRequest = createServerJsonRequest(auRequestDto, httpHeaders);
        OnlineProcessResponse stringStringMap = performPostRequestAndParseResponse(serverJsonRequest, auRequestDto, httpHeaders);
        return stringStringMap;
    }


    public String httpHeaders(String request, HttpHeaders httpHeaders) {
        String environmentProp = httpHeaders.getFirst("CLIENT_ENVIRONMENT");
        String salt_id​ = "";
        String salt​ = "";
        if ("PREPROD".equalsIgnoreCase(environmentProp)) {
            salt_id​ = "04a80a47";
            salt​ = "2fee10a8b139";

        } else {
            salt_id​ = "f06df945";
            salt​ = "3950332ac1a6";
        }

        MessageDigest digest;
        String header​ = "";
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt​.getBytes("UTF-8"));
            String payload = request;
            log.info("payload" + payload);
            // byte[] hashBytes = digest.digest(payload.getBytes("UTF-8")); ​
            String hash​ = Base64.getEncoder()
                    .encodeToString(digest.digest(payload.getBytes("UTF-8")));
            header​ = salt_id​ + ":" + hash​;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        System.out.println("header is " + header​);
        return header​;
    }

    public Map<String, Object> createServerJsonRequest(AadhaarRequestDto auRequestDto, HttpHeaders httpHeaders) {
        Map<String, Object> serverRequest = new HashMap<>();

        try {

            serverRequest.put("TRANSMISSION_DATETIME", String.valueOf(Instant.now().getEpochSecond()));
            serverRequest.put("USER_HANDLE_TYPE", auRequestDto.getUSER_HANDLE_TYPE());
            serverRequest.put("USER_HANDLE_VALUE", auRequestDto.getUSER_HANDLE_VALUE());
            serverRequest.put("CHANNEL_REF_NUMBER", auRequestDto.getTRANSMISSION_REF_NUMBER());
            String serverRequest1 = new Gson().toJson(serverRequest);
            String hash = httpHeaders(serverRequest1, httpHeaders);
            serverRequest.put("CONTENT_HASH", hash);
            serverRequest.put("AOF_ID", auRequestDto.getAOF_ID());

            log.info("Server Request is : " + serverRequest);
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }

        return serverRequest;

    }

    public OnlineProcessResponse performPostRequestAndParseResponse(Map<String, Object> serverRequest, AadhaarRequestDto auRequestDto, HttpHeaders httpHeaders) {
        OnlineProcessResponse finalResponse = new OnlineProcessResponse();
        Map<String, Object> otpResponseMap = new HashMap<>();

        try {

            String responseCode = "";
            String code = "";
            String message = "";
            String responseBody = "";
            String finalProcessStatus = "";
            String infoType = "";
            boolean res = false;
            String uid = auRequestDto.getUSER_HANDLE_VALUE();

            String user_handle_type = auRequestDto.getUSER_HANDLE_TYPE();
            if (user_handle_type.equalsIgnoreCase("AOF_ID")) {
                res = true;
            } else {
                // (1) check with aadharNovalidation
                res = Verhoeff.validateVerhoeff(uid);
                log.info("Is aadhar no valid aftr Verhoeff algo  :" + res);
            }

            if (res) {
                // (2)call aadharOtp service
                log.info("inside if block");


                log.info("AADHAR REQUEST : \n" + serverRequest);

                HttpHeaders header = utility.getApiHttpHeaders("GENERATE_AADHAAR_OTP",
                        auRequestDto.getAOF_ID());

                 String responseMethod = igwExecuteImpl.executeIgwApi(header,serverRequest);
//                Object responseMethod = serviceExecute.executeService(header, serverRequest);
                log.info("responseMethod : {}", responseMethod);
                AadhaarGenerateOtpRes aadhaarGenerateOtpRes = objectMapper.readValue(responseMethod, AadhaarGenerateOtpRes.class);
                log.info("RESPONSE ================== {}", aadhaarGenerateOtpRes);
                responseCode = aadhaarGenerateOtpRes.getAadhaarResponseStatus().getCode();
                log.info("responseCode : " + responseCode);
                responseBody = aadhaarGenerateOtpRes.getAadhaarResponseData().getData();
                log.info("responseBody================= " + responseBody);

                log.info("Response status is=================: "
                        + aadhaarGenerateOtpRes.getAadhaarResponseStatus());

                code = aadhaarGenerateOtpRes.getAadhaarResponseStatus().getCode();
                message = aadhaarGenerateOtpRes.getAadhaarResponseStatus().getMessage();
                String status = aadhaarGenerateOtpRes.getAadhaarResponseStatus().getStatus();

                log.info("Responsecode:  " + code + "ResponseMessage is : "
                        + message + "responsestatus is : " + status);

                finalProcessStatus = "";
                if (code.equalsIgnoreCase("000")) {

                    JSONObject decresp = getDecryptResponse(responseBody);
                    decresp.getJSONObject("response_data");
                    log.info("decresp : {}", decresp);
                    String txn = decresp.getJSONObject("response_data")
                            .getString("txn");

                    String info = decresp.getJSONObject("response_data")
                            .getString("info");

                    log.info("TXN is:  " + txn + "INFO ARRAY is : " + info);

                    String[] otpinfoArray = info.split(",");
                    String gmailold = otpinfoArray[otpinfoArray.length - 1];

                    String gmail = gmailold.replace('}', ' ');
                    if (gmail.contains("NA")) {
                        gmail = "";
                    }

                    String mobile = otpinfoArray[otpinfoArray.length - 2];

                    log.info("Mobile no :" + mobile + "," + "gmail : " + gmail);

                    Map<String, String> txnresp = new HashMap<>();
                    txnresp.put("txn", txn);
                    txnresp.put("mobile", mobile);
                    txnresp.put("gmail", gmail);

                    log.info("SUCCESS CASE");
                    finalProcessStatus = ApplicationConstant.SUCCESS;
                    finalResponse.setProcessStatus(ApplicationConstant.SUCCESS);
                    finalResponse.setResponseType(ApplicationConstant.RESPONSE_TYPE_INFO);
                    finalResponse.setMessageCode(code);
                    finalResponse.setResponseMessage(message);
                    finalResponse.setResponse(txnresp);
                } else {
                    log.info("FAILURE CASE");
                    finalProcessStatus = ApplicationConstant.FAILURE;
                    finalResponse.setProcessStatus(ApplicationConstant.FAILURE);
                    finalResponse.setResponseType(ApplicationConstant.RESPONSE_TYPE_ERROR);
                    finalResponse.setMessageCode(code);
                    finalResponse.setResponseMessage(message);
                }

            } else {
                log.info("inside else block");

                // (3)return hardcoded error message
                responseCode = "200";
                code = "131002";
                message = "Invalid uid";
                finalProcessStatus = "FAILURE";
                infoType = "E";

                finalResponse.setProcessStatus(finalProcessStatus);
                finalResponse.setMessageCode(code);
                finalResponse.setResponseMessage(message);
                finalResponse.setResponseType(infoType);

            }

            otpResponseMap.put("processRequestId", httpHeaders.getFirst("servicename"));
            otpResponseMap.put("responseCode", responseCode);
            otpResponseMap.put("responseSubCode", code);
            otpResponseMap.put("responseMessage", message);
            otpResponseMap.put("refOutData1", ApplicationConstant.EMPTY_STRING);
            otpResponseMap.put("refOutData2", ApplicationConstant.EMPTY_STRING);
            otpResponseMap.put("serverResponse", responseBody);
            otpResponseMap.put("clientResponse", finalResponse);
            otpResponseMap.put("finalProcessStatus", finalProcessStatus);
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
            otpResponseMap.put("error",
                    new Gson().toJson(CommonUtility.getErrorResponse(e)));
        }
        //  commonUtility.storeAadharData(auRequestDto.getVT_ID(), null, eventData, "", "", "", "GENERATE_AADHAAR_OTP");
        return finalResponse;

    }

    public static JSONObject getDecryptResponse(String encryptedResponse)
            throws JSONException {

        JSONObject finalResponse = new JSONObject();
        try {
//            JSONObject jsonObject = new JSONObject(encryptedResponse);
//            JSONObject json = jsonObject.getJSONObject("response_data");
//            Object data = json.opt("data");
            String decoded = new String(
                    DatatypeConverter.parseBase64Binary(encryptedResponse));
            JSONObject js = parse(decoded);
//            JSONObject responseStatus = jsonObject
//                    .getJSONObject("response_status");
            finalResponse.put("response_data", js);
//            finalResponse.put("response_status", responseStatus);
            log.info("Final response: " + finalResponse);

        } catch (Exception e) {
            finalResponse.put("Exception", e);
            e.printStackTrace();
        }
        return finalResponse;

    }


    public static JSONObject parse(String decode) throws JSONException {
        String txn = "";
        String code = "";
        String ts = "";
        String ret = "";
        String info = "";
        JSONObject jsonObject = new JSONObject();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            XML.toJSONObject(decode);

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(decode));
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nListotpres = doc.getElementsByTagName("OtpRes");
            for (int temp = 0; temp < nListotpres.getLength(); temp++) {
                Node nNode = nListotpres.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    txn = eElement.getAttribute("txn");
                    code = eElement.getAttribute("code");
                    ts = eElement.getAttribute("ts");
                    ret = eElement.getAttribute("ret");
                    info = eElement.getAttribute("info");

                }
            }
            jsonObject.put("txn", txn);
            jsonObject.put("code", code);
            jsonObject.put("ts", ts);
            jsonObject.put("ret", ret);
            jsonObject.put("info", info);

        } catch (Exception e) {
            jsonObject.put("EXCEPTION", e);
        }
        return jsonObject;
    }


}
