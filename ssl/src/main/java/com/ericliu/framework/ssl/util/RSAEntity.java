package com.ericliu.framework.ssl.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

/**
 * @Author: <a mailto="liuhaoeric@didichuxing.joyme.com">liuhaoeric</a>
 * Create time: 2018/01/22
 * Description:通过文件或者字符串生产RSA的证书秘钥工具类。实现签名，检查等机制 todo 未实现签名
 */
public class RSAEntity {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    public static final String KEY_X509_TYPE = "X.509";


    private PrivateKey privateKey;
    private X509Certificate cer;

    public RSAEntity(InputStream publicKeyIn, InputStream privateKeyIn) throws Exception {
        CertificateFactory fact = CertificateFactory.getInstance(KEY_X509_TYPE);
        X509Certificate cer = (X509Certificate) fact.generateCertificate(publicKeyIn);
        this.cer = cer;
        this.privateKey = getPrivateKey(privateKeyIn);

    }


////////////////////

    /**
     * get publicKey By inputstream
     *
     * @param in
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws CertificateException
     */
    private X509Certificate getCertificateByIn(InputStream in) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance(KEY_X509_TYPE);
        X509Certificate cer = (X509Certificate) fact.generateCertificate(in);
        return cer;
    }

    /**
     * get privateKey by inputstream
     *
     * @param in
     * @return
     * @throws Exception
     */
    private PrivateKey getPrivateKey(InputStream in) throws Exception {
        byte[] keyDate = FileUtil.loadKeyString(in).getBytes();
        return getPrivateKey(keyDate);
    }

    /**
     * load privatekey by bytes
     *
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private PrivateKey getPrivateKey(byte[] privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(pkcs8KeySpec);
    }
//////////////////////

    /**
     * 校验证书有效期
     *
     * @param in
     * @param date
     * @return
     * @throws CertificateException
     */
    public boolean checkValidity(InputStream in, Date date) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance(KEY_X509_TYPE);
        X509Certificate cer = (X509Certificate) fact.generateCertificate(in);
        try {
            cer.checkValidity();
            return true;
        } catch (CertificateExpiredException e) {
            return false;
        } catch (CertificateNotYetValidException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkDn(InputStream in, String domain) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance(KEY_X509_TYPE);
        X509Certificate cer = (X509Certificate) fact.generateCertificate(in);
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


    public byte[] encryptByPublicKey(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException {
        // 取得公钥
        PublicKey publicKey = cer.getPublicKey();

        // 对数据加密
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public byte[] decryptByPrivateKey(byte[] encodedData) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException {
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

    public PublicKey getPublicKey() {
        return cer.getPublicKey();
    }

    @Override
    public String toString() {
        return "RSAEntity{" +
                "privateKey=" + privateKey +
                ", cer=" + cer +
                '}';
    }
}
