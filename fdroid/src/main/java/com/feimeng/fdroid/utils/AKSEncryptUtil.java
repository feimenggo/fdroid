package com.feimeng.fdroid.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

/**
 * 使用AndroidKeyStore存储密钥/Token
 * Created by feimeng on 2016/10/12.
 */
public class AKSEncryptUtil {
    private static AKSEncryptUtil AKSEncryptUtilInstance;
    private KeyStore keyStore;

    public static AKSEncryptUtil getInstance() {
        if (null == AKSEncryptUtilInstance) {
            AKSEncryptUtilInstance = new AKSEncryptUtil();
        }
        return AKSEncryptUtilInstance;
    }

    private AKSEncryptUtil() {
    }

    private void initKeyStore(Context context, String alias) {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            createNewKey(context, alias);
        }
    }

    private void createNewKey(Context context, String alias) {
        try {
            if (!keyStore.containsAlias(alias)) {
                // 生成密钥库内的一个新条目用
                Calendar cal = Calendar.getInstance();
                Date now = cal.getTime();
                cal.add(Calendar.YEAR, 1);
                Date end = cal.getTime();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    generator.initialize(new KeyGenParameterSpec.Builder
                            (alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                            .build());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    //noinspection deprecation
                    generator.initialize(new KeyPairGeneratorSpec.Builder(context)
                            .setAlias(alias)// 设置别名
                            .setSerialNumber(BigInteger.ONE)// 设置序列号
                            .setStartDate(now).setEndDate(end)// 设置开始日期和结束日期（证书有效期）
                            .setSubject(new X500Principal("CN=" + alias + ", O=Android Authority"))// 设置用于生成密钥签名证书的主题
                            .build());
                }
                // 生成
                generator.generateKeyPair();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加密
     *
     * @param context         上下文
     * @param alias           　别名
     * @param needEncryptWord 　需要加密的字符串
     * @return 密文
     */
    public String encrypt(Context context, String alias, String needEncryptWord) {
        if (!"".equals(alias) && !"".equals(needEncryptWord)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                initKeyStore(context, alias);
            }
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
//            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
                @SuppressLint("GetInstance") Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
//            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);
                inCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                CipherOutputStream cipherOutputStream = new CipherOutputStream(
                        outputStream, inCipher);
                cipherOutputStream.write(needEncryptWord.getBytes("UTF-8"));
                cipherOutputStream.close();
                return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 解密
     *
     * @param context         上下文
     * @param alias           别名
     * @param needDecryptWord 需要解密的字符串
     * @return 明文
     */
    public String decrypt(Context context, String alias, String needDecryptWord) {
        if (!"".equals(alias) && !"".equals(needDecryptWord)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                initKeyStore(context, alias);
            }
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
//            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
                @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
//            output.init(Cipher.DECRYPT_MODE, privateKey);
                cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
                CipherInputStream cipherInputStream = new CipherInputStream(
                        new ByteArrayInputStream(Base64.decode(needDecryptWord, Base64.DEFAULT)), cipher);
                ArrayList<Byte> values = new ArrayList<>();
                int nextByte;
                while ((nextByte = cipherInputStream.read()) != -1) {
                    values.add((byte) nextByte);
                }
                byte[] bytes = new byte[values.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = values.get(i);
                }
                return new String(bytes, 0, bytes.length, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
