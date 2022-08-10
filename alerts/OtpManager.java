package aubank.retail.liabilities.alerts;

import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;

/**
 * @author Naveen Gupta
 * @since 1.0
 */
public interface OtpManager {

    /**
     * It will generate the OTP based on run time parameter provided by client/service
     * it will each be request level
     *
     * @param type            otp type N-> Numeric, A -> AlphaNumeric
     * @param length          otp length 4,6, etc
     * @param expiryTimeInSec otp expiry time in second
     * @return otp
     */
    OtpData getOtp(String type, int length, long expiryTimeInSec) throws AuRlMicroServiceException;

    /**
     * it will generate the OTP based on config provided in properties file.
     * it will be application level
     *
     * @return otp
     */
    OtpData getOtp() throws AuRlMicroServiceException;

    /**
     * Validate the OTP or return true if valid, false if invalid or expired
     *
     * @param id  id -> get from generate otp response
     * @param otp otp -> get from generate otp response
     * @return true/false
     */
    boolean isOtpValid(String id, String otp, String product) throws AuRlMicroServiceException;
}