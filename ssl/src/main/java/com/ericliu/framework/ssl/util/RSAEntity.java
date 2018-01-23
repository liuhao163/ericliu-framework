package com.ericliu.framework.ssl.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;

/**
 * @Author: <a mailto="liuhaoeric@didichuxing.joyme.com">liuhaoeric</a>
 * Create time: 2018/01/22
 * Description:通过文件或者字符串生产RSA的证书秘钥工具类。实现签名，检查等机制
 */
public class RSAEntity {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String KEY_X509_TYPE = "X.509";

    private static final String PRIVIATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----\n";
    private static final String PRIVIATE_KEY_FOOTER = "\n-----END PRIVATE KEY-----";
    private static final String CERTIFICATE_HEADER = "-----BEGIN CERTIFICATE-----\n";
    private static final String CERTIFICATE_FOOTER = "\n-----END CERTIFICATE-----";


    private PrivateKey privateKey;
    private X509Certificate cer;

    RSAEntity(X509Certificate cer, PrivateKey privateKey) {
        this.privateKey = privateKey;
        this.cer = cer;
    }

    /**
     * 建造者类。如果根据pkcs12构造：pkcs12Input和password不能为空了；根据公钥私钥构造：certificateBytes和privateKeyBytes不能为空
     */
    public static class Builder {
        private byte[] certificateBytes;
        private byte[] privateKeyBytes;
        private InputStream pkcs12Input;
        private String password;

        public Builder setPkcs12Input(InputStream pkcs12Input) {
            this.pkcs12Input = pkcs12Input;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setCertificateBytes(byte[] certificateBytes) {
            this.certificateBytes = certificateBytes;
            return this;
        }

        public Builder setPrivateKeyBytes(byte[] privateKeyBytes) {
            this.privateKeyBytes = privateKeyBytes;
            return this;
        }

        /**
         * 根据pkcs12 build，pkcs12Input and password required
         *
         * @return
         * @throws KeyStoreException
         * @throws CertificateException
         * @throws NoSuchAlgorithmException
         * @throws IOException
         * @throws UnrecoverableKeyException
         */
        private RSAEntity buildByPKCS12() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(pkcs12Input, password.toCharArray());
            System.out.println("keystore type = " + ks.getType());
            Enumeration enuml = ks.aliases();
            String keyAlias = null;
            if (enuml.hasMoreElements()) {
                keyAlias = (String) enuml.nextElement();
            }
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, password.toCharArray());
            Certificate cert = ks.getCertificate(keyAlias);
            return new RSAEntity((X509Certificate) cert, prikey);
        }

        /**
         * 根据pkcs12 build，pkcs12Input and password required
         * 根据证书和私钥build rsaEntity certificateBytes and privateKeyBytes required
         *
         * @return
         * @throws InvalidKeySpecException
         * @throws NoSuchAlgorithmException
         * @throws CertificateException
         * @throws UnrecoverableKeyException
         * @throws KeyStoreException
         * @throws IOException
         */
        public RSAEntity build() throws InvalidKeySpecException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyStoreException, IOException {

            if (pkcs12Input != null || password != null) {
                return buildByPKCS12();
            }

            X509Certificate x509Certificate = buildCer(certificateBytes);
            PrivateKey privateKey = null;
            String privateKeyString = new String(privateKeyBytes);
            int start = privateKeyString.indexOf(PRIVIATE_KEY_HEADER);
            int end = privateKeyString.indexOf(PRIVIATE_KEY_FOOTER);
            if (start > -1 && end > -1) {
                privateKeyString = privateKeyString.substring(start + PRIVIATE_KEY_HEADER.length(), end + 1);
                privateKey = buildPemPrivateKey(privateKeyString.getBytes());
            } else {
                privateKey = buildDerPrivateKey(privateKeyBytes);
            }

            if (x509Certificate != null && privateKey != null) {
                return new RSAEntity(x509Certificate, privateKey);
            }
            return null;
        }

        private X509Certificate buildCer(byte[] cer) throws CertificateException {
            CertificateFactory fact = CertificateFactory.getInstance(KEY_X509_TYPE);
            return (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(cer));
        }

        private PrivateKey buildPemPrivateKey(byte[] privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
            byte[] keyBytes = Base64.decodeBase64(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePrivate(pkcs8KeySpec);
        }

        private PrivateKey buildDerPrivateKey(byte[] priKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
            KeySpec keySpec = new PKCS8EncodedKeySpec(priKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }

    }


    //////////////////////
    /**
     * 校验证书有效期
     *
     * @param date
     * @return
     * @throws CertificateException
     */
    public boolean checkValidity(Date date) throws CertificateException {
        try {
            cer.checkValidity(date);
            return true;
        } catch (CertificateExpiredException e) {
            return false;
        } catch (CertificateNotYetValidException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkDn(String domain) {
        String subjectDn = cer.getSubjectDN().getName();
        String[] array = subjectDn.split(",");
        if (array.length == 0) {
            return false;
        }
        for (String entry : array) {
            if (entry.startsWith("CN=")) {
                String entryDomain = entry.replace("CN=", "");
                if (entryDomain.startsWith("*.")) {
                    entryDomain = entryDomain.replace("*.", "");
                }
                if (domain.startsWith("*.")) {
                    domain = domain.replace("*.", "");
                }
                entryDomain.equals(domain);
            }
        }
        return cer.getSubjectDN().getName().contains("CN=" + domain);
    }


    public byte[] encryptByPublicKey(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeySpecException {
        // 取得公钥
        PublicKey publicKey = cer.getPublicKey();

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key encryptKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);

        return cipher.doFinal(data);
    }

    public byte[] decryptByPublicKey(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeySpecException {
        // 取得公钥
        PublicKey publicKey = cer.getPublicKey();

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key encryptKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, encryptKey);

        return cipher.doFinal(data);
    }

    public byte[] encryptByPrivateKey(byte[] encodedData) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeySpecException {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(encodedData);
    }


    public byte[] decryptByPrivateKey(byte[] encodedData) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeySpecException {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encodedData);
    }


    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public X509Certificate getCer() {
        return cer;
    }

    public String getCertificateString() throws CertificateEncodingException {
        return CERTIFICATE_HEADER + Base64.encodeBase64String(cer.getEncoded()) + CERTIFICATE_FOOTER;
    }

    public String getPublicString() throws CertificateEncodingException {
        return Base64.encodeBase64String(cer.getPublicKey().getEncoded());
    }

    public String getPrivateString() throws CertificateEncodingException {
        return PRIVIATE_KEY_HEADER + Base64.encodeBase64String(privateKey.getEncoded()) + PRIVIATE_KEY_FOOTER;
    }

    @Override
    public String toString() {
        return "RSAEntity{" +
                "privateKeyBytes=" + privateKey +
                ", certificateBytes=" + cer +
                '}';
    }
}
