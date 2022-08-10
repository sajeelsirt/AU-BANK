package aubank.retail.liabilities.dtos;

public class AuthRequestDataFromDevice {

    private byte[] encryptedPid;

    public byte[] getEncryptedPid() {
        return encryptedPid;
    }

    public void setEncryptedPid(byte[] encryptedPid) {
        this.encryptedPid = encryptedPid;
    }

    public byte[] getEncryptedHmacBytes() {
        return encryptedHmacBytes;
    }

    public void setEncryptedHmacBytes(byte[] encryptedHmacBytes) {
        this.encryptedHmacBytes = encryptedHmacBytes;
    }

    public String getCertificateIdentifier() {
        return certificateIdentifier;
    }

    public void setCertificateIdentifier(String certificateIdentifier) {
        this.certificateIdentifier = certificateIdentifier;
    }

    public byte[] getEncryptedSessionKey() {
        return encryptedSessionKey;
    }

    public void setEncryptedSessionKey(byte[] encryptedSessionKey) {
        this.encryptedSessionKey = encryptedSessionKey;
    }

    private byte[] encryptedHmacBytes;

    private String certificateIdentifier;

    private byte[] encryptedSessionKey;

    public AuthRequestDataFromDevice() {

    }

    public AuthRequestDataFromDevice(byte[] encryptedPid, byte[] encryptedHmacBytes, String certificateIdentifier, byte[] encryptedSessionKey){

        this.encryptedPid=encryptedPid;
        this.encryptedHmacBytes=encryptedHmacBytes;
        this.certificateIdentifier=certificateIdentifier;
        this.encryptedSessionKey=encryptedSessionKey;
    }



}

