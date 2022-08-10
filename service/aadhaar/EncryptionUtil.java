package aubank.retail.liabilities.service.aadhaar;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * This class provides utility methods that can be used for encryption of
 * various data as per the UIDAI Authentication API.
 *
 * It uses <a href="http://www.bouncycastle.org/">Bouncy Castle APIs</a>.
 *
 * @author Srikanth P Shreenivas
 *
 */
@Component
public final class EncryptionUtil {

    private  final String ASYMMETRIC_ALGO = "RSA/ECB/PKCS1Padding";
    private  final int SYMMETRIC_KEY_SIZE = 256;

    /**
     * Encrypts given data using UIDAI public key
     *
     * @param Certificate
     *            object corresponding to .cer file representing public key. For
     *            example, UIDAI public key file
     * @param data
     *            Data to encrypt
     * @return Encrypted data
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public byte[] encryptUsingPublicKey(X509Certificate certificate, byte[] data)
            throws IOException, GeneralSecurityException {
        // encrypt the session key with the public key
        Cipher pkCipher = Cipher.getInstance(ASYMMETRIC_ALGO);
        pkCipher.init(Cipher.ENCRYPT_MODE, certificate.getPublicKey());
        return pkCipher.doFinal(data);
    }

    /**
     * Encrypts given data using session key
     *
     * @param skey
     *            Session key
     * @param data
     *            Data to encrypt
     * @return Encrypted data
     * @throws InvalidCipherTextException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    public byte[] encryptUsingSessionKey(byte[] skey, byte[] data, byte[] ts) throws InvalidCipherTextException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {

        //Last 12-bytes of ts as IV or Nonce
        byte[] iv = new byte[12];
        System.arraycopy(ts, ts.length-12, iv, 0,iv.length);

        //Last 16-bytes of ts as AAD
        byte[] aad = new byte[16];
        System.arraycopy(ts, ts.length-16, aad,0, aad.length);

        // Authenticated Encryption with Associated Data (AEAD)
        AEADParameters parameters = new AEADParameters( new KeyParameter(skey), 128, iv, aad);

        GCMBlockCipher gcmEngine = new GCMBlockCipher(new AESEngine());
        gcmEngine.init(true, parameters);
        byte[] encMsg = new byte[gcmEngine.getOutputSize(data.length)];
        int encLen = gcmEngine.processBytes(data, 0, data.length, encMsg, 0);
        encLen += gcmEngine.doFinal(encMsg, encLen);
        return encMsg;
    }

    /**
     * Creates a AES key that can be used as session key (skey)
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public byte[] generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(SYMMETRIC_KEY_SIZE);
        SecretKey symmetricKey = kgen.generateKey();
        return symmetricKey.getEncoded();
    }

    /**
     * Returns UIDAI certificate's expiry date in YYYYMMDD format using GMT time
     * zone. It can be used as certificate identifier
     *
     * @param Certificate
     *            object corresponding to .cer file representing public key. For
     *            example, UIDAI public key file
     * @return Certificate identifier for UIDAI public certificate
     */
    public String getCertificateIdentifier(X509Certificate certificate) {
        SimpleDateFormat ciDateFormat = new SimpleDateFormat("yyyyMMdd");
        ciDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return ciDateFormat.format(certificate.getNotAfter());
    }
}

