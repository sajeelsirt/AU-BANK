package aubank.retail.liabilities.service.aadhaar;


import aubank.retail.liabilities.dtos.OnlineProcessResponse;
import aubank.retail.liabilities.util.ApplicationConstant;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Service
@Log
public class CommonUtility {

    @Autowired
    EncryptionUtil encryptionUtil;



//    @Value("${Environment}")
//    private String environment;


    public static OnlineProcessResponse getErrorResponse(Exception e) {
        OnlineProcessResponse response = new OnlineProcessResponse();
        response.setProcessStatus(ApplicationConstant.FAILURE);
        response.setResponseMessage(e.getMessage());
        response.setMessageCode(ApplicationConstant.FAILURE);

        return response;
    }

    public  HashMap<String,String> newaadhaar(String otp, HttpHeaders httpHeaders) {

        String pidts = getCurrentFormattedTimestamp();
        String piddd = "<Pid ts=\"" +
                pidts + "\" ver=\"2.0\" wadh=\"\">" +
                "   <Pv otp=\"" +
                otp + "\" />" +
                "</Pid>";
        try {
            return createAadhaarAuthData(piddd.getBytes(), pidts, piddd,httpHeaders);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String getCurrentFormattedTimestamp() {
        //(IST only) pidTimestamp Format: "2015-12-31T23:59:59"
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        return formattedDate;

    }

    public  HashMap createAadhaarAuthData(byte[] pidBytes, String pidTimestamp, String piddd,HttpHeaders httpHeaders) throws IOException {

        HashMap<String, String> hashMap = new HashMap<>();
        String environment=httpHeaders.getFirst("CLIENT_ENVIRONMENT");


        X509Certificate cert = null;
        try {
            //Load UIDAI public key based on environment
            cert = getUidaiPublicCertificate(environment);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            byte[] skeydata = encryptionUtil.generateSessionKey();
            byte[] encryptedSessionKey = encryptionUtil.encryptUsingPublicKey(cert, skeydata);

            byte[] encryptedPid = encryptionUtil.encryptUsingSessionKey(skeydata, pidBytes, pidTimestamp.getBytes());
//	            byte[] hmac = HashUtil.generateSha256Hash(plainPid);
            byte[] hmac = getSHA256Hash(pidBytes);
            byte[] encryptedHmacBytes = encryptionUtil.encryptUsingSessionKey(skeydata, hmac, pidTimestamp.getBytes());

            String certificateIdentifier = encryptionUtil.getCertificateIdentifier(cert);
            // Need to append TimeStamp bytes before the encrypted PID byte
            byte[] c = new byte[pidTimestamp.getBytes().length + encryptedPid.length];
            System.arraycopy(pidTimestamp.getBytes(), 0, c, 0, pidTimestamp.getBytes().length);
            System.arraycopy(encryptedPid, 0, c, pidTimestamp.getBytes().length, encryptedPid.length);

            //pid.format
            String pidFormat = "X";
            //pid.value

            String encryptedPidInBase64 = new String(org.apache.commons.codec.binary.Base64.encodeBase64(c));
            String encryptedHmacInBase64 = new String(org.apache.commons.codec.binary.Base64.encodeBase64(encryptedHmacBytes));
            String sessionKey = new String(org.apache.commons.codec.binary.Base64.encodeBase64(encryptedSessionKey));

            // now we need to prepare the hashmap

            hashMap.put("HMAC", encryptedHmacInBase64);
            hashMap.put("PID", encryptedPidInBase64);
            hashMap.put("SKEY", sessionKey);
            hashMap.put("CI", certificateIdentifier);
            return hashMap;

            //
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getSHA256Hash(byte[] data) {
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(data);

            return hash; // make it printable

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        return null;

    }


    public static X509Certificate getUidaiPublicCertificate(String environment) throws IOException {
        InputStream is = null;
        try {

            environment=environment.toLowerCase();
            is =   CommonUtility.class.getClassLoader().getResourceAsStream("uidai_validate_auth_"+environment+".cer");
            CertificateFactory certFactory = CertificateFactory.getInstance("X509");

            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(is);
            is.close();
            return cert;
        } catch (Exception e) {
            throw new RuntimeException("Exception while creating auth request", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }
    }




}
