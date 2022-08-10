package aubank.retail.liabilities.service.aadhaar;

import aubank.retail.liabilities.dtos.AuthRequestDataFromDevice;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Component
@Log
public class AadharEncryption {


    @Autowired
    EncryptionUtil encryptionUtil;

    public HashMap<String, String> gettokenizebyte(String aadharno, String environment) {

        String xml = "<TokenizeData id=\"" + aadharno + "\"></TokenizeData>";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String timestamp = format.format(new Date());
        try {
            return createAadhaarAuthData(xml.getBytes(), environment, timestamp);
        } catch (Exception e) {
            log.info("Exception!"+e.getMessage());
        }
        return null;
    }

    public HashMap<String, String> createAadhaarAuthData(byte[] tokenizeDataBytes, String environment, String timestamp)
            throws IOException {

        HashMap<String, String> hashMap = new HashMap<>();
        // Load UIDAI public key based on environment
        X509Certificate cert = getUidaiPublicCertificate(environment);

        // (IST only) timestamp Format: "2015-12-31T23:59:59"
        AuthRequestDataFromDevice authData = PrepareAUAData(cert, tokenizeDataBytes, timestamp);
        try {
            // Need to append TimeStamp bytes before the encrypted PID byte
            byte[] c = new byte[timestamp.getBytes().length + authData.getEncryptedPid().length];
            System.arraycopy(timestamp.getBytes(), 0, c, 0, timestamp.getBytes().length);
            System.arraycopy(authData.getEncryptedPid(), 0, c, timestamp.getBytes().length,
                    authData.getEncryptedPid().length);

            Base64.Encoder encoder = Base64.getEncoder();
            // tokenize_data
            String encryptedPidInBase64 = new String(encoder.encode(c));
            // hmac
            String encryptedHmacInBase64 = new String(encoder.encode(authData.getEncryptedHmacBytes()));
            // skey.ci
            String certificateIdentifier = authData.getCertificateIdentifier();
            // skey.value
            String encryptedSessionKey = new String(encoder.encode(authData.getEncryptedSessionKey()));

            hashMap.put("HMAC", encryptedHmacInBase64);
            hashMap.put("Tokenize_data", encryptedPidInBase64);
            hashMap.put("SKEY_CI", certificateIdentifier);
            hashMap.put("SKEY_VALUE", encryptedSessionKey);
            return hashMap;
        } catch (Exception e) {
            log.info("Exception!"+ e.getMessage());

        }
        return null;
    }

    public AuthRequestDataFromDevice PrepareAUAData(X509Certificate publicKey, byte[] plainPid, String timestamp) {

        try {
            byte[] sessionKey = encryptionUtil.generateSessionKey();
            byte[] encryptedSessionKey = encryptionUtil.encryptUsingPublicKey(publicKey, sessionKey);

            byte[] encryptedPid = encryptionUtil.encryptUsingSessionKey(sessionKey, plainPid, timestamp.getBytes());
            byte[] hmac = generateSha256Hash(plainPid);
            byte[] encryptedHmacBytes = encryptionUtil.encryptUsingSessionKey(sessionKey, hmac, timestamp.getBytes());

            String certificateIdentifier = encryptionUtil.getCertificateIdentifier(publicKey);

            return new AuthRequestDataFromDevice(encryptedPid, encryptedHmacBytes, certificateIdentifier,
                    encryptedSessionKey);

        } catch (Exception e) {
            log.info("Exception!"+ e.getMessage());
            throw new RuntimeException("Error while creating auth reqeust", e);
        }
    }

    /**
     * @param plainPid
     * @return
     */
    private byte[] generateSha256Hash(byte[] plainPid) {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            return d.digest(plainPid);
        } catch (NoSuchAlgorithmException e) {
            log.info("Exception!"+ e.getMessage());
        }
        return null;
    }

    public X509Certificate getUidaiPublicCertificate(String environment) throws IOException {
        // read uidai public cer file

        InputStream certificateResourceStream = new ClassPathResource("uidai_auth_" + environment + ".cer")
                .getInputStream();

        CertificateFactory certFactory = null;
        try {
            certFactory = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certificateResourceStream);
            certificateResourceStream.close();
            return cert;
        } catch (Exception e) {
            throw new RuntimeException("Exception while creating auth request", e);
        } finally {
            if (certificateResourceStream != null) {
                try {
                    certificateResourceStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error while closing certifcate resource stream", e);
                }
            }
        }
    }
}
