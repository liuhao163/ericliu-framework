package com.ericliu.framework.ssl;

import com.ericliu.framework.ssl.util.RSAEntity;
import org.apache.commons.io.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

/**
 * @Author: <a mailto="liuhaoeric@didichuxing.joyme.com">liuhaoeric</a>
 * Create time: 2018/01/23
 * Description:
 */
public class RSATest {
    public static void main(String[] args) throws NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException {
        System.out.println("=============================pem格式证书========================================================");
        File pemCer = new File("/Users/didi/workspace/WAF/sjapi/src/main/resources/certificate.pem");
        File pemPri = new File("/Users/didi/workspace/WAF/sjapi/src/main/resources/key_pcks8");
        try {
            RSAEntity rsapem = new RSAEntity.Builder().setCertificateBytes(FileUtils.readFileToByteArray(pemCer)).setPrivateKeyBytes(FileUtils.readFileToByteArray(pemPri)).build();
            System.out.println("证书：");
            System.out.println(rsapem.getCertificateString());
            System.out.println("私钥：");
            System.out.println(rsapem.getPrivateString());
            String a = "刘浩 liuhaoeric";
            System.out.println("===加密前：===" + a);
            byte data[] = rsapem.encryptByPublicKey(a.getBytes());
            System.out.println("===公钥加密后：===" + new String(data));
            System.out.println("===私钥解密后：===" + new String(rsapem.decryptByPrivateKey(data)));
            System.out.println("=================");
            byte data2[] = rsapem.encryptByPrivateKey(a.getBytes());
            System.out.println("===私钥加密后：===" + new String(data2));
            System.out.println("===公钥解密后：===" + new String(rsapem.decryptByPublicKey(data2)));

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        System.out.println("=====================================================================================");
        System.out.println();
        System.out.println();

        String der2PemCerString = "";
        String der2PemPriString = "";
        System.out.println("=============================der格式证书========================================================");
        File derCer = new File("/Users/didi/workspace/WAF/sjapi/src/main/resources/certificate.der");
        File derPri = new File("/Users/didi/workspace/WAF/sjapi/src/main/resources/key_pcks8.der");
        try {
            RSAEntity rsaDer = new RSAEntity.Builder().setCertificateBytes(FileUtils.readFileToByteArray(derCer)).setPrivateKeyBytes(FileUtils.readFileToByteArray(derPri)).build();
            System.out.println("证书：");
            der2PemCerString = rsaDer.getCertificateString();
            System.out.println(der2PemCerString);
            System.out.println("私钥：");
            der2PemPriString = rsaDer.getPrivateString();
            System.out.println(der2PemPriString);
            String a = "刘浩 liuhaoeric";
            System.out.println("===加密前：===" + a);
            byte data[] = rsaDer.encryptByPublicKey(a.getBytes());
            System.out.println("===公钥加密后：===" + new String(data));
            System.out.println("===私钥解密后：===" + new String(rsaDer.decryptByPrivateKey(data)));
            System.out.println("=================");
            byte data2[] = rsaDer.encryptByPrivateKey(a.getBytes());
            System.out.println("===私钥加密后：===" + new String(data2));
            System.out.println("===公钥解密后：===" + new String(rsaDer.decryptByPublicKey(data2)));

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        System.out.println("=====================================================================================");
        System.out.println();
        System.out.println();

        System.out.println("=============================der证书用pem格式存 进行加密解密========================================================");
        System.out.println("==der转换后的pem cer==");
        System.out.println(der2PemCerString);
        System.out.println("==der转换后的pem pri==");
        System.out.println(der2PemPriString);
        try {
            RSAEntity der2PemEntity = new RSAEntity.Builder().setCertificateBytes(der2PemCerString.getBytes()).setPrivateKeyBytes(der2PemPriString.getBytes()).build();
            String a = "刘浩 liuhaoeric";
            System.out.println("===加密前：===" + a);
            byte data[] = der2PemEntity.encryptByPublicKey(a.getBytes());
            System.out.println("===公钥加密后：===" + new String(data));
            System.out.println("===私钥解密后：===" + new String(der2PemEntity.decryptByPrivateKey(data)));
            System.out.println("=================");
            byte data2[] = der2PemEntity.encryptByPrivateKey(a.getBytes());
            System.out.println("===私钥加密后：===" + new String(data2));
            System.out.println("===公钥解密后：===" + new String(der2PemEntity.decryptByPublicKey(data2)));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        System.out.println("=====================================================================================");
        System.out.println();
        System.out.println();

        System.out.println("=============================pkcs12 进行加密解密========================================================");
        File pkcs12File = new File("/Users/didi/workspace/WAF/sjapi/src/main/resources/certificatepkcs12.pfx");
        try {
            RSAEntity pkcs12Entity = new RSAEntity.Builder().setPkcs12Input(new FileInputStream(pkcs12File)).setPassword("123456").build();
            String a = "刘浩 liuhaoeric";
            System.out.println("===加密前：===" + a);
            byte data[] = pkcs12Entity.encryptByPublicKey(a.getBytes());
            System.out.println("===公钥加密后：===" + new String(data));
            System.out.println("===私钥解密后：===" + new String(pkcs12Entity.decryptByPrivateKey(data)));
            System.out.println("=================");
            byte data2[] = pkcs12Entity.encryptByPrivateKey(a.getBytes());
            System.out.println("===私钥加密后：===" + new String(data2));
            System.out.println("===公钥解密后：===" + new String(pkcs12Entity.decryptByPublicKey(data2)));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        System.out.println("=====================================================================================");
        System.out.println();
        System.out.println();


    }

}
