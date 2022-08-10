package aubank.retail.liabilities.util;

import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.java.Log;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static aubank.retail.liabilities.util.ApplicationConstant.IGW;
import static aubank.retail.liabilities.util.ApplicationConstant.SERVICE;


@Service
@Log
public class HttpCalling {


    public static final String IGW_EXECUTE = "executeOnlineProcess";
    @Value("${vconnect.service.endpoint}")
    private String serviceUrl;

    @Value("${igw.url}")
    private String igwUrl;


    public String callExecuteApi(HttpHeaders httpHeaders, Object requestMap, String systemName) throws JsonProcessingException, AuRlMicroServiceException {

        String url = "";

        String response = null;
        switch (systemName) {

            case SERVICE:
                url = serviceUrl;
                break;
            case IGW:
                url = igwUrl + IGW_EXECUTE;
                break;

            default:
                throw new AuRlMicroServiceException(ApplicationConstant.FAILURE);
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            System.out.println("===========================URL is===================================");
            System.out.println(url);
            HttpEntity<Object> entity = new HttpEntity<>(requestMap, httpHeaders);
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            response = exchange.getBody();
        } catch (Exception ex) {
            log.info(ex.getMessage());
            ex.printStackTrace();
            throw new AuRlMicroServiceException(ex.getMessage());
        }

        return response;
    }

    public ResponseEntity<String> sendPostRequest(String url, String requestObj, HttpHeaders headers) {


        try {
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustStrategy() {

                        @Override
                        public boolean isTrusted(X509Certificate[] chain,
                                                 String authType) throws CertificateException {
                            return true;
                        }
                    }).build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(
                    sslContext);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);

            HttpEntity<String> entity = new HttpEntity<>(requestObj, headers);
            RestTemplate restTemplate = new RestTemplate(requestFactory);

            return restTemplate.postForEntity(url, entity, String.class);

        } catch (ResourceAccessException e) {
            log.info(ApplicationConstant.LOG_ERROR);
            e.printStackTrace();

            String responseMessage = e.getMessage();
            String reasonPhrase = e.getRootCause().toString();


        } catch (HttpServerErrorException e) {
            log.info(ApplicationConstant.LOG_ERROR);
            e.printStackTrace();

            String code = String.valueOf(e.getStatusCode());
            String statusText = e.getStatusText();
            String responseBody = e.getResponseBodyAsString();


        } catch (HttpClientErrorException exception) {
            log.info(ApplicationConstant.LOG_ERROR);
            exception.printStackTrace();

            String responseCode = String
                    .valueOf(exception.getStatusCode().value());
            String reasonPhrase = exception.getStatusCode().getReasonPhrase();
            String detailMessage = exception.getStatusText();
            String responseBody = exception.getResponseBodyAsString();


        } catch (Exception e) {
            log.info(ApplicationConstant.LOG_ERROR);
            e.printStackTrace();
        }

        return null;
    }

}
