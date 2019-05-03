package com.lxgy.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Gryant
 */
public class Server {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(2000);

        System.out.println("服务端准备就绪~");
        System.out.println("服务端信息：" + serverSocket.getInetAddress() + " ,port:" + serverSocket.getLocalPort());

        // 等待客户端连接
        for (; ; ) {
            // 得到客户端
            Socket client = serverSocket.accept();
            // 客户端构建异步线程
            ClientHandler clientHandler = new ClientHandler(client);
            // 启动线程
            clientHandler.start();
        }
    }

    /**
     * 客户端消息处理
     */
    private static class ClientHandler extends Thread{
        private Socket socket;
        private boolean flag = true;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();

            System.out.println("新的客户端连接信息：" + socket.getInetAddress() + " ,port:" + socket.getPort());

            try {
                // 得到打印流，用于数据输出；服务器回送数据使用
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                do {

                    // 拿到客户端发送的数据
                    String clientStr = socketInput.readLine();
                    if ("bye".equalsIgnoreCase(clientStr)) {
                        flag = false;
                        socketOutput.println("bye");
                    } else {
                        System.out.println(clientStr);
                        socketOutput.println("服务端回送数据的长度为：" + clientStr.length());
                    }
                } while (flag);

                socketInput.close();
                socketOutput.close();
            } catch (IOException e) {
                System.out.println("连接异常断开");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("客户端已关闭");
        }
    }
}
