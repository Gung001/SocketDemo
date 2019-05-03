package com.lxgy.socket;

import java.io.*;
import java.net.*;

/**
 * @author Gryant
 */
public class Client {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket();

        // 设置超时时间
        socket.setSoTimeout(3000);

        // 连接本地，端口2000，超时时间3000
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(),2000),3000);

        System.out.println("已发起服务端连接，并进入后续流程~");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " ,port:" + socket.getLocalPort());
        System.out.println("服务端信息：" + socket.getInetAddress() + " ,port:" + socket.getPort());

        // 发送数据
        try {
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }

        // 释放资源
        socket.close();
        System.out.println("客户端已退出~");
    }

    private static void todo(Socket socket) throws IOException {

        // 获取键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        // 服务端输出流（数据发送到服务端）
        OutputStream outputStream = socket.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        // 服务端输入流（从服务端接收数据）
        InputStream inputStream = socket.getInputStream();
        BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;
        do {

            // 键盘读取
            String clientStr = input.readLine();
            socketPrintStream.println(clientStr);

            // 服务器读取一行
            String socketStr = socketInput.readLine();
            if ("bye".equals(socketStr)) {
                flag = false;
            } else {
                System.out.println(socketStr);
            }
        } while (flag);

        // 资源释放
        socketInput.close();
        socketPrintStream.close();
    }
}
