#java对SSL进行认证签名的工具类
支持对ssl加密、解密
支持der-->pem证书的转化
支持pcks12证书分离


#构造RSAEntity
用构造这模式构造实体eg:RSAEntity rsapem = new RSAEntity.Builder().setCertificateBytes(FileUtils.readFileToByteArray(pemCer)).setPrivateKeyBytes(FileUtils.readFileToByteArray(pemPri)).build();
                         
```java
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
        
```

##加密
```java

            byte data[] = rsapem.encryptByPublicKey(a.getBytes());
            byte data2[] = rsapem.encryptByPrivateKey(a.getBytes());
   
```

##解密
```java
rsapem.decryptByPrivateKey(data)
rsapem.decryptByPublicKey(data2)
```

##验证证书有效性

###验证证书时间
```java
rsapem.checkValidity
```


###验证证书的域名
```java
rsapem.checkDN
```



##测试生成自签名证书的
<b>第二步，将私钥转成pkcs8,建议做否则会出现异常</b>
1. openssl req -newkey rsa:1024 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem
2. openssl pkcs8 -topk8 -nocrypt -in key.pem -out key_pcks8 -outform PEM
3. openssl pkcs12 -export -inkey key.pem -in certificate.pem -out certificatepkcs12.pfx  

<b>der</b>
1. openssl req -newkey rsa:1024 -nodes -keyout key.der -x509 -days 365 -out certificate.der -outform DER
2. openssl pkcs8 -topk8 -nocrypt -in key.der -out key_pcks8.der -outform DER

## ISSUS
后期会完善证书格式适配，将der、pck12的格式的证书转成pem