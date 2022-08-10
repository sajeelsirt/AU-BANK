package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.dtos.ResponseDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.util.ApplicationConstant;
import aubank.retail.liabilities.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationImpl {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ServiceExecuteImpl serviceExecute;

    @Autowired
    Utility utility;


    @Value(("${alerts.config.path}"))
    String alertConfigPath;

    static ObjectNode configuration;


    public ObjectNode raiseAlert(String alertId, String vtId, String alertInputContent) throws IOException, AuRlMicroServiceException {
        return raiseAlert(alertId, vtId, alertInputContent, "0");
    }

    public ObjectNode raiseAlert(String alertId, String vtId, JsonNode alertInputContent) throws IOException, AuRlMicroServiceException {
        return raiseAlert(alertId, vtId, String.valueOf(alertInputContent), "0");
    }

    public ObjectNode raiseAlert(String alertId, String vtId, String alertInputContent, String isOTP) throws IOException, AuRlMicroServiceException {

        System.out.println("!!!!!!!!!!!!!!!!!!Sending Alert Raised for below details!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("alertId=" + alertId);
        System.out.println("alertContent=" + alertInputContent);

        ObjectNode alertData = objectMapper.createObjectNode();

        /*InputStream resource = new ClassPathResource(Constants.ALERT_FILE_NAME).getInputStream();
        String alertsConfiguration;
        try (Reader reader = new InputStreamReader(resource)) {
            alertsConfiguration = CharStreams.toString(reader);
        }
        JsonNode alertConfig = objectMapper.readTree(alertsConfiguration).get(alertId);
         */

        if (configuration == null) {
            configuration = objectMapper.readValue(new File(alertConfigPath), ObjectNode.class);
        }
        JsonNode alertConfig = configuration.get(alertId);
        if (alertConfig == null) {
            throw new AuRlMicroServiceException("Please pass valid ALERT ID");
        }
        Map variables = getAllVariablesForAlerts(vtId, alertInputContent, alertConfig);
        String isSmsRequired = alertConfig.has("SMS_REQUIRED") ? alertConfig.get("SMS_REQUIRED").asText() : "N";
        String isEmailRequired = alertConfig.has("EMAIL_REQUIRED") ? alertConfig.get("EMAIL_REQUIRED").asText() : "N";
        String isWhatsAppRequired = alertConfig.has("WHATSAPP_REQUIRED") ? alertConfig.get("WHATSAPP_REQUIRED").asText() : "N";

        alertData.put("SMS_REQUIRED", isSmsRequired);
        alertData.put("EMAIL_REQUIRED", isEmailRequired);
        alertData.put("WHATSAPP_REQUIRED", isWhatsAppRequired);
        alertData.put("IS_OTP", isOTP);

        if (!Strings.isBlank(isSmsRequired) && isSmsRequired.equalsIgnoreCase("Y")) {
            // Send SMS
            String smsTo = replaceVariables(alertConfig.get("SMS_TO").asText(), variables);
            String smsBody = replaceVariables(alertConfig.get("SMS_BODY").asText(), variables);
            alertData.put("SMS_TO", smsTo);
            alertData.put("SMS_BODY", smsBody);

        }
        if (!Strings.isBlank(isEmailRequired) && isEmailRequired.equalsIgnoreCase("Y")) {
            // Send Email
            String emailTo = replaceVariables(alertConfig.get("EMAIL_TO").asText(), variables);
            String emailCcTo = replaceVariables(alertConfig.get("EMAIL_CC_TO").asText(), variables);
            String emailBccTo = replaceVariables(alertConfig.get("EMAIL_BCC_TO").asText(), variables);
            String emailSubject = replaceVariables(alertConfig.get("EMAIL_SUBJECT").asText(), variables);
            String emailBody = replaceVariables(alertConfig.get("EMAIL_BODY").asText(), variables);
            String emailAttachmentPaths = replaceVariables(alertConfig.has("EMAIL_ATTACHMENT_PATHS") ? alertConfig.get("EMAIL_ATTACHMENT_PATHS").asText() : "", variables);
            String emailAttachmentFileName = replaceVariables(alertConfig.has("EMAIL_ATTACHMENT_FILE_NAME") ? alertConfig.get("EMAIL_ATTACHMENT_FILE_NAME").asText() : "", variables);
            alertData.put("EMAIL_TO", emailTo);
            alertData.put("EMAIL_CC_TO", emailCcTo);
            alertData.put("EMAIL_BCC_TO", emailBccTo);
            alertData.put("EMAIL_SUBJECT", emailSubject);
            alertData.put("EMAIL_BODY", emailBody);
            alertData.put("EMAIL_ATTACHMENT_PATHS", emailAttachmentPaths);
            alertData.put("EMAIL_ATTACHMENT_FILE_NAME", emailAttachmentFileName);
        }
        if (!Strings.isBlank(isWhatsAppRequired) && isWhatsAppRequired.equalsIgnoreCase("Y")) {
            // Send Whatsapp
            String whatsappTo = replaceVariables(alertConfig.get("WHATSAPP_TO").asText(), variables);
            String whatsappBody = replaceVariables(alertConfig.get("WHATSAPP_BODY").asText(), variables);
            alertData.put("WHATSAPP_TO", whatsappTo);
            alertData.put("WHATSAPP_BODY", whatsappBody);
        }
        alertData.put("VT_ID", vtId);
        sendAlertViaService(alertData, vtId);
        return alertData;
    }


    private Map getAllVariablesForAlerts(String vtId, String alertInputContent, JsonNode alertConfig) throws IOException, AuRlMicroServiceException {

        Map variables = new HashMap<String, String>();
        ArrayNode variableDetails = (ArrayNode) alertConfig.get("VARIABLES");
        String vtObjectData = null;

        for (int i = 0; i < variableDetails.size(); i++) {

            String varName = variableDetails.get(i).get("VAR_NAME").asText();
            String dataSource = variableDetails.get(i).get("DATA_SOURCE").asText();
            String value = variableDetails.get(i).get("VALUE").asText();
            String isNullableAllowed = variableDetails.get(i).has("IS_NULLABLE") ? variableDetails.get(i).get("IS_NULLABLE").asText() : "N";
            String defaultValue = variableDetails.get(i).has("DEFAULT_VALUE") ? variableDetails.get(i).get("DEFAULT_VALUE").asText() : "";
            String varValue = null;

            switch (dataSource) {
                case "VT": {
                    vtObjectData = getVtObject(vtId, vtObjectData);
                    varValue = getVariableFromVt(vtObjectData, value);
                    break;
                }
                case "INPUT": {
                    varValue = getVariableFromInput(alertInputContent, value);
                    break;
                }
                case "VTLINK": {
                    varValue = getVTLink(vtId);
                    break;
                }
                case "CONSTANT": {
                    varValue = value;
                    break;
                }
                default:
                    break;
            }

            if (!Strings.isEmpty(varValue)) {
                variables.put(varName, varValue);
            } else if (Strings.isEmpty(varValue) && isNullableAllowed.equalsIgnoreCase("Y")) {
                variables.put(varName, "");
            } else if (!Strings.isEmpty(defaultValue)) {
                variables.put(varName, defaultValue);
            } else if (Strings.isEmpty(varValue) && isNullableAllowed.equalsIgnoreCase("Y")) {
                variables.put(varName, "");
            } else {
                throw new AuRlMicroServiceException("Please pass valid variable value for: " + varName);
            }
        }
        return variables;
    }

    private String getVtObject(String vtId, String vtObjectData) throws IOException, AuRlMicroServiceException {

        if (Strings.isBlank(vtObjectData) || vtObjectData.length() < 10) {
            Map<String, Object> getVrtObjRequestMap = new HashMap<>();
            getVrtObjRequestMap.put("P_OBJECT_PRI_KEY_1", vtId);
            getVrtObjRequestMap.put("P_MOBILE_NUMBER", "");
            getVrtObjRequestMap.put("P_WORKFLOW_GROUP_ID", "");
            getVrtObjRequestMap.put("P_OBJECT_DATA", "");
            getVrtObjRequestMap.put("P_CREATED_BY", "");
            getVrtObjRequestMap.put("P_PRODUCT", "");
            getVrtObjRequestMap.put("P_CHANNEL", "");
            getVrtObjRequestMap.put("P_SUB_CHANNEL", "");

            ResponseDto responseDto = null;
            //dbesExecute.executeDbesApi(utility.getApiHttpHeaders("GETORCREATEVTOBJECT", "VCONNECT"), getVrtObjRequestMap);
            JsonNode vtRecord = objectMapper.readTree(String.valueOf(responseDto.getResponse())).get(0);
            if (vtRecord != null) {
                JsonNode vtObject = objectMapper.readTree(vtRecord.get("OBJECT").asText());
                return vtObject.get("OBJECT_DATA").asText();
            } else {
                throw new AuRlMicroServiceException( "Please pass VT ID.");
            }

        } else {
            return vtObjectData;
        }
    }

    private String getVariableFromVt(String objectData, String path) {
        return JsonPath.read(objectData, path);
    }

    private String getVariableFromInput(String alertInputContent, String path) {
        return JsonPath.read(alertInputContent, path);
    }

    private String getVTLink(String vtId) {
        return utility.getPwaHomePageRedirectUrl(vtId);
    }

    private String replaceVariables(String content, Map<String, String> variables) throws AuRlMicroServiceException {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String varName = entry.getKey();
            String varValue = entry.getValue();
            content = content.replace(varName, varValue);
        }
        return content;
    }

    @Async
    private void sendAlertViaService(ObjectNode alertData, String vtId) {

        System.out.println("!!!!!!!!!!!!!!!!!!Sending Alert!!!!!!!!!!!!!!!!!!!!!!!!");
        HttpHeaders httpHeaders = utility.getServiceHttpHeaders("SEND_ALERTS", vtId);
        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(alertData));
            Object response = serviceExecute.executeService(httpHeaders, alertData);
            System.out.println(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (AuRlMicroServiceException e) {
            e.printStackTrace();
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Async
    public void updateAlertStatus(String vtId, String alertId, String status) {
        ObjectNode alertContent = objectMapper.createObjectNode();
        alertContent.put("VT_ID", vtId);
        alertContent.put("ALERT_ID", alertId);
        alertContent.put("STATUS", status);
        alertContent.put("ID", ApplicationConstant.EMPTY_STRING);

        HttpHeaders httpHeaders = utility.getServiceHttpHeaders("UPDATE_ALERT_STATUS", vtId);
        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(alertContent));

            Object response = serviceExecute.executeService(httpHeaders, alertContent);
            System.out.println(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (AuRlMicroServiceException e) {
            e.printStackTrace();
        }
    }

    public ObjectNode sendAppointmentAlerts(String alertId, String vtId, JsonNode alertInputContent) throws IOException, AuRlMicroServiceException {
        return raiseAlert(alertId, vtId, String.valueOf(alertInputContent),"0");
    }

    @Async
    public void marketingCampaignEvents(JsonNode vtObjectData, String vtId) {

    }



}
