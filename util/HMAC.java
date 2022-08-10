package aubank.retail.liabilities.util;


import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HMAC {
    private static final String ENCODING_SCHEME = "UTF-8";

    public static String SHA512(String data, String salt) throws AuRlMicroServiceException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(ENCODING_SCHEME));
            byte[] bytes = md.digest(data.getBytes(ENCODING_SCHEME));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new AuRlMicroServiceException(BusinessConstant.HMAC_FAILURE_MESSAGE+e.getMessage());
        }
    }
    public static String SHA256(String data, String salt) throws AuRlMicroServiceException {

        System.out.println("String for HMAC:"+ data);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(ENCODING_SCHEME));
            byte[] bytes = md.digest(data.getBytes(ENCODING_SCHEME));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new AuRlMicroServiceException(BusinessConstant.HMAC_FAILURE_MESSAGE+e.getMessage());
        }
    }
    public static String SHA256WithoutSalt(String plainText) throws AuRlMicroServiceException {
        try{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] encodedhash = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(encodedhash);
    } catch (NoSuchAlgorithmException e) {
        throw new AuRlMicroServiceException(BusinessConstant.HMAC_FAILURE_MESSAGE+e.getMessage());
    }

    }
}
