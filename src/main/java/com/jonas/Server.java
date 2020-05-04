package com.jonas;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Server
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-25
 */
public class Server {
    private final String SERVER_KEY_STORE_PASSWORD = "123456";
    private final String SERVER_TRUST_KEY_STORE_PASSWORD = "123456";

    public SSLServerSocket createServerSocket(int port) {
        try {
            SSLContext ctx = SSLContext.getInstance("SSL");

            KeyStore ks = KeyStore.getInstance("JKS");
            //加载keystore中的条目以及进行密码校验
            ks.load(Server.class.getClassLoader().getResourceAsStream("kserver.keystore"), SERVER_KEY_STORE_PASSWORD.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());

            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(Server.class.getClassLoader().getResourceAsStream("tserver.keystore"), SERVER_TRUST_KEY_STORE_PASSWORD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(tks);

            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLServerSocket serverSocket = (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(port);
            //需要进行客户端认证，即双向认证
            serverSocket.setNeedClientAuth(true);
            return serverSocket;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        final ServerSocket ss = server.createServerSocket(8080);
        System.out.println("ssl server startup at port " + 8080);
        while (true) {
            Socket socket = ss.accept();
            new Thread(() -> {
                BufferedInputStream bis = null;
                BufferedOutputStream bos = null;
                try {
                    bis = new BufferedInputStream(socket.getInputStream());
                    bos = new BufferedOutputStream(socket.getOutputStream());

                    byte[] buffer = new byte[20];
                    int length = bis.read(buffer);
                    System.out.println("Receive: " + new String(buffer, 0, length).toString());

                    bos.write("Hello".getBytes());
                    bos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != socket) {
                            socket.close();
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
            }).start();
        }
    }
}
