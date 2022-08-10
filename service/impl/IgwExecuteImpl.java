package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.util.ApplicationConstant;
import aubank.retail.liabilities.util.HttpCalling;
import aubank.retail.liabilities.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.java.Log;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;


/**
 *
 * Service :   Service layer  to consume api via igw call
 *
 * @author Himanshi Manchanda
 *
 */

@Service
@Log
public class IgwExecuteImpl {

    @Autowired
    Utility utility;

    @Autowired
    HttpCalling httpCalling;

    @Autowired
    ObjectMapper objectMapper;


    public String executeIgwApi(HttpHeaders httpHeaders, Map<String,Object> requestMap) throws JsonProcessingException, AuRlMicroServiceException {
        utility.checkHeaders(httpHeaders);

        return httpCalling.callExecuteApi(httpHeaders, requestMap, ApplicationConstant.IGW);

    }

    public ResponseEntity<String> sendPostRequest(String url,HttpHeaders headers, String requestObj) throws JsonProcessingException {


        log.info("Inside sendPostRequest with");
        log.info("URL :: " + url);
        log.info("requestObj===>" + requestObj);
        log.info("headers " + headers);
        ResponseEntity<String> response;
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            HttpEntity<String> entity = new HttpEntity(requestObj, headers);
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            response = restTemplate.postForEntity(url, entity, String.class);
            log.info("responseObj===>"+response);
        }
        catch (HttpServerErrorException exception) {
            // log.error(Strings.LOG_ERROR + processRequestId);
            exception.printStackTrace();
            String responseCode = String
                    .valueOf(exception.getStatusCode().value());
            String reasonPhrase = exception.getStatusCode().getReasonPhrase();
            String detailMessage = exception.getStatusText();
            String responseBody = exception.getResponseBodyAsString();

            log.info("responseBody --"+responseBody);

            ObjectNode objectNode = objectMapper.createObjectNode();

            objectNode.put("Responsecode", responseCode);
            objectNode.put("reasonPhrase", reasonPhrase);
            objectNode.put("detailMessage", detailMessage);
            objectNode.put("responseBody", responseBody);


            return new ResponseEntity<String>(objectNode.toString(), HttpStatus.CREATED);

        }


        catch (Exception exception) {
            String responseCode = "500";
            String reasonPhrase = exception.getMessage();
            String detailMessage = exception.getMessage();

            ObjectNode objectNode = objectMapper.createObjectNode();

            objectNode.put("Responsecode", responseCode);
            objectNode.put("reasonPhrase", reasonPhrase);
            objectNode.put("detailMessage", detailMessage);


            return new ResponseEntity<String>(objectNode.toString(), HttpStatus.CREATED);
        }
        return response;

    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
