package com.ericliu.framework.ssl;

import static com.ericliu.framework.ssl.RSACoder.KEY_ALGORITHM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.junit.Before;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;

public class RSACoderTest {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public static String loadPrivateKey(InputStream in) throws Exception {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }


    @Before
    public void setUp() throws Exception {
//        Map<String, Object> keyMap = RSACoder.initKey();
//
//        privateKey = RSACoder.getPrivateKey(keyMap);


        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream("/Users/didi/github/ericliu-framework/ssl/src/main/resources/certificate.pem");
        X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
        publicKey = cer.getPublicKey();

        //证书过期
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.YEAR,100);
        try {
            cer.checkValidity(calendar.getTime());
        } catch (CertificateExpiredException e) {
            e.printStackTrace();
        } catch (CertificateNotYetValidException e) {
            e.printStackTrace();
        }

        System.out.println(cer.getSubjectDN().getName());




        FileInputStream pri = new FileInputStream("/Users/didi/github/ericliu-framework/ssl/src/main/resources/key_pcks8");
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(loadPrivateKey(pri).getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        privateKey= keyFactory.generatePrivate(pkcs8KeySpec);


        //        BASE64Decoder base64Decoder= new BASE64Decoder();
//        byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);
        System.err.println("公钥: \n\r" + publicKey);
        System.out.println("私钥 \n\r" + privateKey);
    }

    @Test
    public void test() throws Exception {
        System.err.println("公钥加密——私钥解密");
        String inputStr = "abc";
        byte[] data = inputStr.getBytes();

        byte[] encodedData = RSACoder.encryptByPublicKey(data, publicKey.getEncoded());



        // 对数据解密
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedData = cipher.doFinal(encodedData);

        String outputStr = new String(decodedData);
        System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);
        assertEquals(inputStr, outputStr);
    }
}