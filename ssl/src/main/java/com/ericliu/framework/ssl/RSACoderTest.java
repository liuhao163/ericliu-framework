package com.ericliu.framework.ssl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.*;

import com.ericliu.framework.ssl.util.RSAEntity;
import org.junit.Before;
import org.junit.Test;

public class RSACoderTest {

    private RSAEntity RSAEntity;

    @Before
    public void setUp() throws Exception {
//        Map<String, Object> keyMap = RSACoder.initKey();
//
//        privateKey = RSACoder.getPrivateKey(keyMap);

//
//        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream("/Users/didi/github/ericliu-framework/ssl/src/main/resources/certificate.pem");
//        X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
//        publicKey = cer.getPublicKey();
//
//        //证书过期
//        Calendar calendar=Calendar.getInstance();
//        calendar.add(Calendar.YEAR,100);
//        try {
//            cer.checkValidity(calendar.getTime());
//        } catch (CertificateExpiredException e) {
//            e.printStackTrace();
//        } catch (CertificateNotYetValidException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(cer.getSubjectDN().getName());
//
//
//
//
        FileInputStream pri = new FileInputStream("/Users/didi/github/ericliu-framework/ssl/src/main/resources/key_pcks8");
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(loadPrivateKey(pri).getBytes()));
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        privateKey= keyFactory.generatePrivate(pkcs8KeySpec);
//
//
//        //        BASE64Decoder base64Decoder= new BASE64Decoder();
////        byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);

        RSAEntity = new RSAEntity(is, pri);

//        System.err.println("公钥: \n\r" + publicKey);
//        System.out.println("私钥 \n\r" + privateKey);
    }

    @Test
    public void test() throws Exception {
        System.err.println("公钥加密——私钥解密");
        String inputStr = "abc";
        byte[] data = inputStr.getBytes();

//        byte[] encodedData = RSACoder.encryptByPublicKey(data, publicKey.getEncoded());
        byte[] encodedData = RSAEntity.encryptByPublicKey(data);


        // 对数据解密
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedData = RSAEntity.decryptByPrivateKey(encodedData);

        String outputStr = new String(decodedData);
        System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);
        assertEquals(inputStr, outputStr);
    }
}