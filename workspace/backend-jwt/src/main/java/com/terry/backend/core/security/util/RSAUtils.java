package com.terry.backend.core.security.util;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class RSAUtils {

    private static final int keySize = 4096;
    private static final String INSTANCE_TYPE = "RSA/ECB/PKCS1Padding";

    private static PublicKey publicKey = null;
    private static PrivateKey privateKey = null;

    public static void generateKeypair() throws NoSuchAlgorithmException {
        final SecureRandom random = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize, random);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public static PublicKey getPublicKey(){
        return publicKey;
    }

    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        byte[] painTextByte = cipher.doFinal(plainText.getBytes());

        return base64EcodeToString(painTextByte);
    }

    public static String decrypt(String plainText) throws Exception {
        byte[] encryptedPlainTextByte = Base64.getDecoder().decode(plainText.getBytes());

        Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(encryptedPlainTextByte), StandardCharsets.UTF_8);
    }

    private static String base64EcodeToString(byte[] byteDate) {
        return Base64.getEncoder().encodeToString(byteDate);
    }

    private static PrivateKey convertPrivateKey(PrivateKey privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] privateKeyByte = Base64.getDecoder().decode(privateKey.getEncoded());
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyByte));
    }

    private static PublicKey convertPublicKey(PublicKey publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKey.getEncoded());

        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyByte));
    }
}
