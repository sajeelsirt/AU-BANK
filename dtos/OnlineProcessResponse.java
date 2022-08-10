package aubank.retail.liabilities.dtos;

import com.google.gson.annotations.SerializedName;

public class OnlineProcessResponse
{
    @SerializedName(value = "response_code")
    private String responseCode;

    @SerializedName(value = "response_message")
    private String responseMessage;

    @SerializedName(value = "response")
    private Object response;

    @SerializedName(value = "message_code")
    private String messageCode;

    @SerializedName(value = "process_status")
    private String processStatus;

    @SerializedName(value = "response_type")
    private String responseType;

    public OnlineProcessResponse() { }

    public OnlineProcessResponse(String responseCode, String responseMessage, String response, String messageCode, String processStatus)
    {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.response = response;
        this.messageCode = messageCode;
        this.processStatus = processStatus;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

}

