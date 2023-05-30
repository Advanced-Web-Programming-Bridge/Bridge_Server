package gachon.bridge.userservice.utils;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import gachon.bridge.userservice.base.BaseException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static gachon.bridge.userservice.base.BaseErrorCode.DECRYPTION_ERROR;
import static gachon.bridge.userservice.base.BaseErrorCode.ENCRYPTION_ERROR;

@Component
public class AES256Util {
    private String iv;
    private Key keySpec;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public AES256Util(@Value("${encrypt.secretKey}") String key) throws UnsupportedEncodingException {
        try {
            this.iv = key.substring(0, 16);
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");

            int len = b.length;
            if (len > keyBytes.length) {
                len = keyBytes.length;
            }

            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            this.keySpec = keySpec;

        } catch (Exception e) {
            log.error(e.getMessage()); // 예외를 콘솔에 출력
        }
    }

    /**
     * AES256 으로 암호화
     *
     * @param str 암호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String encrypt(String str) throws BaseException {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));

            byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
            String enStr = new String(Base64.encodeBase64(encrypted));

            return enStr;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(ENCRYPTION_ERROR);
        }
    }

    /**
     * AES256으로 암호화된 txt를 복호화
     *
     * @param str 복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String decrypt(String str) throws BaseException {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));

            byte[] byteStr = Base64.decodeBase64(str.getBytes());

            return new String(c.doFinal(byteStr), "UTF-8");

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DECRYPTION_ERROR);
        }
    }
}
