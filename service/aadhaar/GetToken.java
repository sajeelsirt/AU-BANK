package aubank.retail.liabilities.service.aadhaar;

import aubank.retail.liabilities.util.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;

@Component
@Log
public class GetToken {


    @Autowired
    Utility utility;

    @Autowired
    AadharEncryption aadharEncryption;

    @Autowired
    ObjectMapper objectMapper;


    public String callDoTokenizationApi(HttpHeaders httpHeaders, String aadhaarnum, String igwReqId) {
        String getTokenUrl = httpHeaders.getFirst("TOKEN_URL");
        String serverClientIp = httpHeaders.getFirst("CLIENT_IP");
        String environment = httpHeaders.getFirst("CLIENT_ENVIRONMENT");
        String env = environment.toLowerCase();
        ObjectNode jsonObjectForInsert = objectMapper.createObjectNode();

        String[] tags = {"com:ResponseCode", "com:ResponseMessage", "ns2:status", "ns2:uid_token", "ns2:code",
                "ns2:message"};
        String[] result = new String[6];

        String stanvalue = utility.getRandomNo();
        String tokenid = "";

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            docFactory.setNamespaceAware(true);
            Element rootElement = doc.createElement("soapenv:Envelope");
            rootElement.setAttribute("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
            rootElement.setAttribute("xmlns:com", "http://schemas.auf.com/integration/common");
            rootElement.setAttribute("xmlns:aad", "http://schemas.auf.com/integration/account/aadhar");
            Element header = doc.createElement("soapenv:Header");
            Element aufHeaderRequest = doc.createElement("com:AUFHeaderRequest");

            Element requestId = doc.createElement("com:RequestId");
            requestId.setTextContent(stanvalue);
            Element originatingChannel = doc.createElement("com:OriginatingChannel");
            originatingChannel.setTextContent("DEC");
            aufHeaderRequest.appendChild(requestId);
            aufHeaderRequest.appendChild(originatingChannel);
            header.appendChild(aufHeaderRequest);
            Element body = doc.createElement("soapenv:Body");
            Element aadhartokenizerequest = doc.createElement("aad:AadharTokenizeRequest");

            Element aadharheaders = doc.createElement("aad:headers");

            Element actortype = doc.createElement("aad:actor_type");
            actortype.setTextContent("CUSTOMER");

            Element channelcode = doc.createElement("aad:channel_code");
            channelcode.setTextContent("WEB_APP");

            Element stan = doc.createElement("aad:stan");
            stan.setTextContent(stanvalue);

            Element userhandletype = doc.createElement("aad:user_handle_type");
            userhandletype.setTextContent("USERNAME");

            Element userhandlevalue = doc.createElement("aad:user_handle_value");
            userhandlevalue.setTextContent("AUBANK");

            Element loc = doc.createElement("aad:location");
            loc.setTextContent("");

            Element transdatetime = doc.createElement("aad:transmission_datetime");

            long a = System.currentTimeMillis();

            transdatetime.setTextContent(Long.toString(a));

            String serverip = "";

            Element clientip = doc.createElement("aad:client_ip");
            if (env.equalsIgnoreCase("preprod")) {
                serverip = serverClientIp;
            } else {
                serverip = serverClientIp;
            }
            clientip.setTextContent(serverip);

            Element operationmode = doc.createElement("aad:operation_mode");
            operationmode.setTextContent("SYSTEM");

            Element chanelversion = doc.createElement("aad:channel_version");
            chanelversion.setTextContent("2.5");

            Element functionsubcode = doc.createElement("aad:function_sub_code");
            functionsubcode.setTextContent("DEFAULT");

            Element functioncode = doc.createElement("aad:function_code");
            functioncode.setTextContent("TOKENIZE");

            aadharheaders.appendChild(actortype);
            aadharheaders.appendChild(channelcode);
            aadharheaders.appendChild(stan);
            aadharheaders.appendChild(userhandletype);
            aadharheaders.appendChild(userhandlevalue);
            aadharheaders.appendChild(loc);
            aadharheaders.appendChild(transdatetime);
            aadharheaders.appendChild(clientip);
            aadharheaders.appendChild(operationmode);
            aadharheaders.appendChild(chanelversion);
            aadharheaders.appendChild(functionsubcode);
            aadharheaders.appendChild(functioncode);

            Element aadharrequest = doc.createElement("aad:request");
            Element clientrefnum = doc.createElement("aad:client_ref_number");
            clientrefnum.setTextContent(stanvalue);

            Element purpose = doc.createElement("aad:purpose");
            purpose.setTextContent("TOKENIZE");

            Element ekycflag = doc.createElement("aad:ekyc_flag");
            ekycflag.setTextContent("n");

            HashMap<String, String> hashMap = aadharEncryption.gettokenizebyte(aadhaarnum, env);

            Element hmac = doc.createElement("aad:hmac");
            hmac.setTextContent(hashMap.get("HMAC"));

            Element tokenizedata = doc.createElement("aad:tokenize_data");
            tokenizedata.setTextContent(hashMap.get("Tokenize_data"));

            Element skey = doc.createElement("aad:skey");
            Element value = doc.createElement("aad:value");
            value.setTextContent(hashMap.get("SKEY_VALUE"));

            Element ci = doc.createElement("aad:ci");
            ci.setTextContent(hashMap.get("SKEY_CI"));

            skey.appendChild(value);
            skey.appendChild(ci);

            aadharrequest.appendChild(clientrefnum);
            aadharrequest.appendChild(purpose);
            aadharrequest.appendChild(ekycflag);
            aadharrequest.appendChild(hmac);
            aadharrequest.appendChild(tokenizedata);
            aadharrequest.appendChild(skey);

            aadhartokenizerequest.appendChild(aadharheaders);
            aadhartokenizerequest.appendChild(aadharrequest);

            body.appendChild(aadhartokenizerequest);

            rootElement.appendChild(header);
            rootElement.appendChild(body);
            doc.appendChild(rootElement);

            String request = utility.convertXmlDocToString(doc);

            log.info("request=============" + request);

            log.info("URL : " + getTokenUrl);


            String response = utility.callXMLService(doc, getTokenUrl);


            result = utility.parseXMLDataArray(response, tags);


            if (result[1].equalsIgnoreCase("SUCCESS")) {
                tokenid = result[3];
            }

            jsonObjectForInsert.put("requestid", igwReqId);
            jsonObjectForInsert.put("leadid", "");
            jsonObjectForInsert.put("processname", "TOKENIZATION");
            jsonObjectForInsert.put("workflowgorupid", "AU_CC");
            jsonObjectForInsert.put("request", request);
            jsonObjectForInsert.put("response", response);
            jsonObjectForInsert.put("status", result[1]);
            jsonObjectForInsert.put("clientrequest", "");
            jsonObjectForInsert.put("clientresponse", "");
            jsonObjectForInsert.put("url", getTokenUrl);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return tokenid;

    }

}

