package aubank.retail.liabilities.alerts;

import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.util.ApplicationConstant;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


/**
 * @author Naveen Gupta
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class OtpManagerImpl implements OtpManager {
    private final OtpCacheRepo otpCacheRepo;
    private final LoadingCache<String, String> loadingCache;

    @Value("${otp.cache.type}")
    private String cacheType;

    @Value("${otp.type}")
    private String otpType;

    @Value("${otp.length}")
    private int otpLength;

    @Value("${otp.expiry.time}")
    private long otpExpiryTime;

    @Override
    public OtpData getOtp(String type, int length, long expiryTimeInSec) throws AuRlMicroServiceException {
        switch (cacheType) {
            case "R":
                return getOtpFromRedis(type, length, expiryTimeInSec);
            case "I":
                return getOtpFromInMemory(type, length, expiryTimeInSec);
            default:
                throw new AuRlMicroServiceException("OTPCT - Invalid otp cache type configuration");
        }
    }

    public OtpData getOtpFromRedis(String type, int length, long expiryTimeInSec) throws AuRlMicroServiceException {
        String otp = getSecureRandomCode(type, length);
        OtpData otpData = new OtpData();
        otpData.setOtp(otp);
        otpData.setExpiryTime(expiryTimeInSec);
        otpCacheRepo.save(otpData);
        return otpData;
    }

    public OtpData getOtpFromInMemory(String type, int length, long expiryTimeInSec) throws AuRlMicroServiceException {
        String otp = getSecureRandomCode(type, length);
        String uuid = UUID.randomUUID().toString();
        loadingCache.put(uuid, otp);
        OtpData otpData = new OtpData();
        otpData.setOtp(otp);
        otpData.setId(uuid);
        otpData.setExpiryTime(expiryTimeInSec);
        return otpData;
    }

    @Override
    public OtpData getOtp() throws AuRlMicroServiceException {
        return getOtp(otpType, otpLength, otpExpiryTime);
    }

    @Override
    public boolean isOtpValid(String id, String otp, String product) throws AuRlMicroServiceException {
        switch (cacheType) {
            case "R":
                return isOtpValidInRedis(id, otp, product);
            case "I":
                return isOtpValidInMemory(id, otp, product);
            default:
                throw new AuRlMicroServiceException("OTPCT - Invalid otp cache type configuration");
        }
    }

    public boolean isOtpValidInRedis(String id, String userOtp, String product) {
        Optional<OtpData> otpData = otpCacheRepo.findById(id);
        if (otpData.isPresent()) {
            String systemOtp = otpData.get().getOtp();
            return systemOtp.equals(userOtp);
        }
        return false;
    }

    public boolean isOtpValidInMemory(String id, String userOtp, String product) {
        try {
            String systemOtp = loadingCache.get(id);
            return systemOtp.equals(userOtp);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getSecureRandomCode(String type, int digit) throws AuRlMicroServiceException {
        switch (type) {
            case "A":
                String salt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                return getRandom(salt, digit);
            case "N":
                salt = "0123456789";
                return getRandom(salt, digit);
            default:
                throw new AuRlMicroServiceException("OTPCT - Invalid otp cache type configuration");
        }
    }

    public static String getRandom(String saltChars, int digit) {
        StringBuilder salt = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        while (salt.length() < digit) {
            int index = (int) (rnd.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        return salt.toString();
    }
}