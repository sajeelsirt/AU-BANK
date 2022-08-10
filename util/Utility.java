package aubank.retail.liabilities.util;


import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


@Service
public class Utility {
    Logger log = LoggerFactory.getLogger(Utility.class);
    @Value(("${ORG_ID}"))
    public String orgId;
    @Value(("${APP_ID}"))
    public String appId;
    @Value(("${APP_CLIENT_SECRET}"))
    public String appClientSecret;
    @Value("${REDIRECT_URL}")
    public String redirectUrl;

    public String getRandomDigit() {
        SecureRandom rnd = new SecureRandom();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String getRandomNo() {
        return "DEC" + UUID.randomUUID();
    }

    public String convertXmlDocToString(Document doc) {
        String requestString = "";
        try {
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StringWriter outWriter = new StringWriter();
            StreamResult result = new StreamResult(outWriter);
            transformer.transform(source, result);
            requestString = outWriter.toString();
        } catch (Exception e) {
            log.error("Exception ==> ", e.getMessage());
        }
        return requestString;
    }

    public String callXMLService(Document doc, String url) throws Exception {
        String resultData = "";
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

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter outWriter = new StringWriter();
            StreamResult result = new StreamResult(outWriter);
            transformer.transform(source, result);
            String requestString = outWriter.toString();

            requestFactory.setHttpClient(httpClient);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/xml");
            HttpEntity<String> entity = new HttpEntity<>(requestString, headers);
            RestTemplate restTemplate = new RestTemplate(requestFactory);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    url, entity, String.class);
            log.info("RESPONSE======" + responseEntity);

            resultData = responseEntity.getBody();

        } catch (Exception e) {
            resultData = e.getMessage();
            log.error("Exception ==> ", e.getMessage());
        }
        return resultData;
    }

    public String callXMLServiceV1(String request, String url) throws Exception {
        String resultData = "";
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

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            String requestString = request;

            requestFactory.setHttpClient(httpClient);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/xml");
            HttpEntity<String> entity = new HttpEntity<>(requestString, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    url, entity, String.class);
            log.info("RESPONSE======" + responseEntity);

            resultData = responseEntity.getBody();

        } catch (Exception e) {
            resultData = e.getMessage();
            log.error("Exception ==> ", e.getMessage());
        }
        return resultData;
    }

    public String[] parseXMLDataArray(String xmlOutput, String[] elements) {
        String[] result = null;
        if (elements != null) {
            result = new String[elements.length];
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(new StringReader(
                        xmlOutput)));
                doc.getDocumentElement().normalize();
                for (int j = 0; j < elements.length; j++) {
                    NodeList nList = doc.getElementsByTagName(elements[j]);
                    for (int i = 0; i < nList.getLength(); i++) {
                        Node nNode = nList.item(i);
                        result[j] = nNode.getTextContent();
                    }
                    log.info("<TAG_" + elements[j] + ">" + result[j] + "</TAG_"
                            + elements[j] + ">");

                }

            } catch (Exception e) {
                log.error("Exception ==> ", e.getMessage());

            }
        }
        return getNormalizedObject(result);
    }

    public static String[] getNormalizedObject(Object[] pwRequest) {
        String[] reqArr = null;
        if (pwRequest != null) {
            reqArr = new String[pwRequest.length];
            for (int i = 0; i < pwRequest.length; i++) {
                reqArr[i] = pwRequest[i] != null ? pwRequest[i].toString() : "";
            }
        }
        return reqArr;
    }

    public String base64Encode(byte[] bytes) {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
    }

    public byte[] base64Decode(String str) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(str);
    }

    public HttpHeaders getApiHttpHeaders(String apiName, String loginId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("orgid", orgId);
        httpHeaders.add("appid", appId);
        httpHeaders.add("apiname", apiName);
        httpHeaders.add("requestid", appId + loginId + System.currentTimeMillis());
        httpHeaders.add("loginid", loginId);
        httpHeaders.add("logsrequired", "Y");
        return httpHeaders;
    }

    public HttpHeaders getServiceHttpHeaders(String apiName, String loginId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("orgid", orgId);
        httpHeaders.add("appid", appId);
        httpHeaders.add("servicename", apiName);
        httpHeaders.add("requestid", appId + loginId + System.currentTimeMillis());
        httpHeaders.add("loginid", loginId);
        httpHeaders.add("clientsecret", appClientSecret);
        return httpHeaders;
    }

    public String getPwaHomePageRedirectUrl(String vtId) {
        String webUrl = redirectUrl + "VT=" + vtId;
        return webUrl;
    }

    public String getPwaVBUniversalUrl() {
        String webUrl = redirectUrl + "product=VB";
        return webUrl;
    }

    public String getPwaHomePageRedirectUrlWithMultipleParams(Map<String, String> paramMap) {
        StringBuilder req = new StringBuilder();

        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            req.append(key).append("=").append(value).append("&");
        }
        String webUrl = redirectUrl + req;
        return webUrl;
    }

    public static String trimMultiSpaces(String str) {
        if (str != null) {
            str = str.replaceAll("\\n", " ").replaceAll(" +", " ").trim();
        }
        return str;
    }

    public static String convertToddMMyyyy(String dateToParse) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formetedDate = "";
        try {
            if (dateToParse != null && !dateToParse.isEmpty()) {
                Date date = originalFormat.parse(dateToParse);
                formetedDate = targetFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formetedDate;
    }

    public static String submissionDateParse(String submission_date) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formetedDate = "";
        Date date;
        try {
            date = originalFormat.parse(submission_date);
            formetedDate = targetFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formetedDate;

    }

    public static String convertDOBtoyyyymmdd(String dob) {
        Date tempdoi = null;
        String finaldob = null;
        try {
            if (dob.contains("/"))
                dob = dob.replaceAll("/", "-");

            if (dob.indexOf('-') == 4) {
                tempdoi = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
            } else if (dob.indexOf('-') == 2) {
                tempdoi = new SimpleDateFormat("dd-MM-yyyy").parse(dob);
            }
            if (tempdoi != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                finaldob = formatter.format(tempdoi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finaldob;
    }

    public String getTimeInMilliSeconds(){
        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();
        return String.valueOf(timestamp);
    }

    public String createAofId(AofRequestDto requestDto)
    {
        String aofId = "AOF"+requestDto.getRecordIdentifier2()+requestDto.getRecordIdentifier3()
                + ZonedDateTime.now().toInstant().toEpochMilli();
        return aofId;
    }

    public void checkHeaders(HttpHeaders httpHeaders) throws AuRlMicroServiceException {
        Boolean status = true;
        if (!httpHeaders.containsKey("orgid")) {
            throw new AuRlMicroServiceException("Headers does not contain the key orgid");
        }
        if (!httpHeaders.containsKey("appid")) {
            throw new AuRlMicroServiceException("Headers does not contain the key appid");
        }
        if (!httpHeaders.containsKey("apiname")) {
            throw new AuRlMicroServiceException("Headers does not contain the key apiname");
        }
        if (!httpHeaders.containsKey("requestid")) {
            throw new AuRlMicroServiceException("Headers does not contain the key requestid");
        }
    }
}
