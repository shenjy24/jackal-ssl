## 证书生成步骤
### 服务端
1.生成服务端私钥，并且导入到服务端KeyStore文件中
```
keytool -genkey -alias serverkey -keystore kserver.keystore -storetype PKCS12
```

2.根据私钥，导出服务端证书
```
keytool -export -alias serverkey -keystore kserver.keystore -file server.crt
```

3.将服务端证书，导入到客户端的Trust KeyStore中
```
keytool -import -alias serverkey -file server.crt -keystore tclient.keystore
```

### 客户端
1.生成客户端私钥，并且导入到客户端KeyStore文件中
```
keytool -genkey -alias clientkey -keystore kclient.keystore -storetype PKCS12
```

2.根据私钥，导出客户端证书
```
keytool -export -alias clientkey -keystore kclient.keystore -file client.crt
```

3.将客户端证书，导入到服务端的Trust KeyStore中
```
keytool -import -alias clientkey -file client.crt -keystore tserver.keystore
```