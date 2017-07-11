/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package doorbox.portal.utils;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;

/**
 *
 * @author torinw
 */
public class Crypto {
    public static void main(String[] args) {
        //System.out.println(md5("Foo + bar == baz"));
        //System.out.println("url: " + mod_auth_token_encodeUrl("/images", "coth123", "/t/i/r/u/b/1/2/3/img1234.png", System.currentTimeMillis()));
        
        String password = "000006";
        try {
            String hash = encrypt_SHA1(password);
            System.out.println("encrypt_SHA1(" + password + ": " + hash);

            hash = randomHash();
            System.out.println("randomHash(): " + hash);
        } catch (Exception e) {
            System.out.println("Crypto failed" + e.getMessage());
            e.printStackTrace();
        }
    }
            
    /**
     * 
     * @param password The plaintext string
     * @return A SHA1 encrypted hash of the plaintext string
     * @throws java.lang.Exception
     */
    public static String encrypt_SHA1(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(password.getBytes("UTF-8"));
        byte raw[] = md.digest();
        String hash = (new BASE64Encoder()).encode(raw);
        return hash;
    }

    public static String randomHash() {
        Random r = new Random(System.currentTimeMillis());

        String randomNum = null;

        do {
            randomNum = Long.toHexString(r.nextLong());
        } while (randomNum.length() != 16);

        return randomNum;
    }

    public static String md5(String plaintext) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] raw = m.digest(plaintext.getBytes("UTF-8"));
            return new BigInteger(1, raw).toString(16);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String mod_auth_token_encodeUrl(String uriPrefix, String secret, String path, long time) {
        String dechexTime = Long.toHexString(time);
        String token = md5(secret + path + dechexTime);
        return uriPrefix + "/" + token + "/" + dechexTime + path;
    }
    
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String message, String key)
        throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(message.getBytes()));
    }
}