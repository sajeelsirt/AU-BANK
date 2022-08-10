package aubank.retail.liabilities.alerts;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

/**
 * Redis Hash for holding otp, it will auto expiry after expiry time
 *
 * @author Naveen Gupta
 * @since 1.0
 */
@Data
@RedisHash(value = "VPS_OTP_DATA")
public final class OtpData implements Serializable {
    private static final long serialVersionUID = 29902111724661L;

    @Id
    private String id;

    private String otp;

    @TimeToLive
    private Long expiryTime;
}
