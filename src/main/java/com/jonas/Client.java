package com.jonas;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.KeyStore;

/**
 * Client
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-25
 */
public class Client {
    private final String CLIENT_KEY_STORE_PASSWORD = "123456";
    private final String CLIENT_TRUST_KEY_STORE_PASSWORD = "123456";

    public SSLSocket createSocket(String host, int port) {
        try {
            SSLContext ctx = SSLContext.getInstance("SSL");

            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(Client.class.getClassLoader().getResourceAsStream("kclient.keystore"), CLIENT_KEY_STORE_PASSWORD.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());

            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(Client.class.getClassLoader().getResourceAsStream("tclient.keystore"), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(tks);

            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            return (SSLSocket) ctx.getSocketFactory().createSocket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SSLSocket sslSocket = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            Client client = new Client();
            sslSocket = client.createSocket("127.0.0.1", 8080);

            bis = new BufferedInputStream(sslSocket.getInputStream());
            bos = new BufferedOutputStream(sslSocket.getOutputStream());

            bos.write("Hello".getBytes());
            //使用AppOutputStream类进行发送
            bos.flush();

            byte[] buffer = new byte[20];
            int length = bis.read(buffer);
            System.out.println(new String(buffer, 0, length));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != sslSocket) {
                    sslSocket.close();
                }
                if (null != bis) {
                    bis.close();
                }
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
