package com.lxgy.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * @author Gryant
 */
public class Server {

    public static final int PORT = 20000;
    public static final int LOCAL_PORT = 20001;

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = createServerSocket();

        initServerSocket(serverSocket);

        // 绑定到本地端口上
        serverSocket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 50);

        System.out.println("服务器准备就绪~");
        System.out.println("服务器信息：" + serverSocket.getInetAddress() + " P:" + serverSocket.getLocalPort());

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

    private static void todo(ServerSocket socket) throws SocketException {

    }

    private static void initServerSocket(ServerSocket serverSocket) throws SocketException {

        // 是否复用为完全关闭的地址端口
        serverSocket.setReuseAddress(true);

        // 等效于Socket#setReceiveBufferSize
        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);

        // 设置serverSocket#accept超时时间
//        serverSocket.setSoTimeout(2000);

        // 设置性能参数
        serverSocket.setPerformancePreferences(1, 1, 1);
    }

    private static ServerSocket createServerSocket() throws IOException {

        // 创建基础的ServerSocket
        ServerSocket serverSocket = new ServerSocket();

        // 绑定到本地端口20000上，并且设置当前可允许等待连接的队列为50个
//        serverSocket = new ServerSocket(PORT);
        // 等同于上面方案
//        serverSocket = new ServerSocket(PORT, 50);
        // 同上
//        serverSocket = new ServerSocket(PORT, 50, Inet4Address.getLocalHost());

        return serverSocket;
    }

    private static class ClientHandler extends Thread {

        private Socket socket;
        private Boolean done = false;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();

            System.out.println("新客户端连接：" + socket.getInetAddress() + " P:" + socket.getPort());

            try {

                // 得到套接字流
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                byte[] buffer = new byte[256];
                int read = inputStream.read(buffer);
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, read);

                // byte
                byte be = byteBuffer.get();
                // char
                char c = byteBuffer.getChar();
                // int
                int i = byteBuffer.getInt();
                // bool
                boolean b = byteBuffer.get() == 1;
                // Long
                long l = byteBuffer.getLong();
                // float
                float f = byteBuffer.getFloat();
                // double
                double d = byteBuffer.getDouble();
                // String
                int pos = byteBuffer.position();
                String str = new String(buffer, pos, read - pos - 1);

                System.out.println("收到数据：" + read + " 数据：" +
                        "\n" + be +
                        "\n" + c +
                        "\n" + i +
                        "\n" + b +
                        "\n" + l +
                        "\n" + f +
                        "\n" + d +
                        "\n" + str
                );

                outputStream.write(buffer, 0, read);

                /**
                byte[] buffer = new byte[128];
                int read = inputStream.read(buffer);
                if (read > 0) {
                    int value = Tools.byteArray2Int(buffer);
                    System.out.println("收到数据：" + read + " 数据：" + value);

                    // 回送收到的数据
                    outputStream.write(buffer, 0, read);
                } else {
                    System.out.println("没有收到数据：" + read);

                    outputStream.write(new byte[]{0});
                }

                 */

                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ingored) {

                }
            }

            System.out.println("客户端连接断开：" + socket.getInetAddress() + " P:" + socket.getPort());
        }
    }
}
