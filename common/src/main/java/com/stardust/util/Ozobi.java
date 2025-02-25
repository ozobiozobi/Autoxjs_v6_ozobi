package com.stardust.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;



/**
 * 都看到这了, 不加个QQ群吗 >.> ------------- 1014521824 ---------------这里这里-------
 */
public class Ozobi {
    public static String ozobiString = "ozobiozobi";
    public static String qqGroup1 = "1014521824";
    public static byte[] AESKey = new byte[]{7,125,-13,80,-123,-109,7,-80,-67,103,-85,-97,12,41,106,118};

    public static byte[] strToBytes(String str){
        return str.getBytes();
    }

    public static String bytesToStr(byte[] bytes){
        return new String(bytes);
    }

    public static SecretKey bytesToSecretKey(byte[] keyBytes, String algorithm){
        return new SecretKeySpec(keyBytes, algorithm);
    }

    public static SecretKey genPBEKeyFromPwd(String password, int keySize, String algorithm) throws Exception {
        // 生成盐值（salt）
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // 定义迭代次数
        int iterationCount = 65536;

        // 使用PBEKeySpec和SecretKeyFactory生成密钥
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keySize);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec);
    }
    /**
     * 加密输入字节数组并返回加密后的字节数组。
     *
     * @param inputBytes 要加密的原始字节数组
     * @param secretKey  加密密钥
     * @param algorithm 加密算法
     * @return 加密后的字节数组
     * @throws Exception 如果加密过程中发生错误
     */
    public static byte[] encrypt(byte[] inputBytes, SecretKey secretKey, String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(inputBytes);
    }
    /**
    * 加密目标文件并写入指定文件
     *
     * @param inputFile 要加密的文件
     * @param outputFile 输出到文件
     * @param secretKey  加密密钥
     * @param algorithm 加密算法
    * */
    public static void encrypt(File inputFile, File outputFile, SecretKey secretKey,String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            fis.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            fos.write(outputBytes);
        }
    }
    /**
     * 解密输入字节数组并返回解密后的字节数组。
     *
     * @param inputBytes 要解密的字节数组
     * @param secretKey  解密密钥
     * @return 解密后的字节数组
     * @throws Exception 如果解密过程中发生错误
     */
    public static byte[] decrypt(byte[] inputBytes, SecretKey secretKey,String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(inputBytes);
    }
    /**
     * 解密目标文件并写入指定文件
     *
     * @param inputFile 要解密的文件
     * @param outputFile 输出到文件
     * @param secretKey  解密密钥
     * @param algorithm 解密算法
     * */
    public static void decrypt(File inputFile, File outputFile, SecretKey secretKey,String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            fis.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            fos.write(outputBytes);
        }
    }
    public static Boolean authenticate(Context context){
        return context.getPackageName().contains("ozobi");
    }
}