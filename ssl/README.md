#java对SSL进行认证签名的工具类


##加密

##解密

##签名

##验证证书有效性

###验证证书时间

###验证证书的域名

##测试生成自签名证书的
<b>第二步，将私钥转成pkcs8,建议做否则会出现异常</b>
1. openssl req -newkey rsa:1024 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem
2. openssl pkcs8 -topk8 -nocrypt -in key.pem -out key_pcks8 -outform PEM

## ISSUS
后期会完善证书格式适配，将der、pck12的格式的证书转成pem