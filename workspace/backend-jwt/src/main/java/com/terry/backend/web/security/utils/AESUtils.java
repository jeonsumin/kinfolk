package com.terry.backend.web.security.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class AESUtils {

  /**
   * AES256 암호화 남에꺼 긁어옴
   *
   * @param msg 메시지
   * @param key 암호화 키
   * @return 암호화된 메시지
   * @throws Exception 오류
   */
  public static String encrypt(String msg, String key) throws Exception {
    SecureRandom random = new SecureRandom();
    byte bytes[] = new byte[20];
    random.nextBytes(bytes);
    byte[] saltBytes = bytes;

    // Password-Based Key Derivation function 2
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    // 70000번 해시하여 256 bit 길이의 키를 만든다.
    PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, 70000, 256);
    SecretKey secretKey = factory.generateSecret(spec);
    SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

    // 알고리즘/모드/패딩
    // CBC : Cipher Block Chaining Mode
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secret);
    AlgorithmParameters params = cipher.getParameters();

    // Initial Vector(1단계 암호화 블록용)
    byte[] ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
    byte[] encryptedTextBytes = cipher.doFinal(msg.getBytes("UTF-8"));
    byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];

    System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
    System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
    System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length,
        encryptedTextBytes.length);

    return Base64.getEncoder().encodeToString(buffer);
  }

  /**
   * AES256 복호화 남에꺼 긁어옴
   *
   * @param msg 암호화 메시지
   * @param key 암호 키
   * @return 복호된 메시지
   * @throws Exception 오류
   */
  public static String decrypt(String msg, String key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

    ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(msg));

    byte[] saltBytes = new byte[20];
    buffer.get(saltBytes, 0, saltBytes.length);
    byte[] ivBytes = new byte[cipher.getBlockSize()];
    buffer.get(ivBytes, 0, ivBytes.length);
    byte[] encryoptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes.length];
    buffer.get(encryoptedTextBytes);

    if (log.isDebugEnabled()) {
      String bufferStr = printBytes(buffer.array());
      String saltStr = printBytes(saltBytes);
      String ivStr = printBytes(ivBytes);
      String encrypotedTextStr = printBytes(encryoptedTextBytes);

      log.debug(String.format("buffer <%s>", bufferStr));
      log.debug(String.format("saltBytes <%s>", saltStr));
      log.debug(String.format("ivBytes <%s>", ivStr));
      log.debug(String.format("encrypotedTextStr <%s>", encrypotedTextStr));
    }

    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, 70000, 256);
    SecretKey secretKey = factory.generateSecret(spec);
    SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
    byte[] decryptedTextBytes = cipher.doFinal(encryoptedTextBytes);

    return new String(decryptedTextBytes);
  }

  private static String printBytes(byte[] bytes) {
    StringBuffer buffer = new StringBuffer();
    for (byte b : bytes) {
      buffer.append(String.format("%02x", b & 0xff));
    }
    return buffer.toString();
  }

}
