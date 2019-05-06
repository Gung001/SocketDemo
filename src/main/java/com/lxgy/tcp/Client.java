package com.lxgy.tcp;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * @author Gryant
 */
public class Client {

    public static final int PORT = 20000;
    public static final int LOCAL_PORT = 20001;

    public static void main(String[] args) throws IOException {

        Socket socket = createSocket();

        initSocket(socket);

        // 手动链接到本地20000端口，超时时间3s
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 3000);

        System.out.println("已发起服务器连接，并进入后续流程~");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务端信息：" + socket.getInetAddress() + " P:" + socket.getPort());

        try {
            // 发送接收数据
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }

        // 释放资源
        socket.close();
        System.out.println("客户端已关闭~");

    }

    private static void todo(Socket client) throws IOException {

        // 得到socket输出流
        OutputStream outputStream = client.getOutputStream();

        // 得到socket输入流
        InputStream inputStream = client.getInputStream();
        byte[] buffer = new byte[256];

        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

        // byte
        byteBuffer.put((byte) 126);

        // char
        char c = 'a';
        byteBuffer.putChar(c);

        // int
        int i = 121212;
        byteBuffer.putInt(i);

        // bool
        boolean b = true;
        byteBuffer.put(b ? (byte) 1 : (byte) 0);

        // long
        long l = 34567899;
        byteBuffer.putLong(l);

        // float
        float f = 23.23f;
        byteBuffer.putFloat(f);

        // double
        double d = 12.2345643;
        byteBuffer.putDouble(d);

        // String
        String str = "Hello 你好";
        byteBuffer.put(str.getBytes());

        // 发送到服务器
        outputStream.write(buffer, 0, byteBuffer.position() + 1);

        // 收到服务器回送数据
        int read = inputStream.read(buffer);
        System.out.println("收到回送数据：" + read);

        /**
        // 发送到服务器
        byte[] bytes = Tools.int2ByteArray(121212);
        outputStream.write(bytes);

        // 接收服务器返回
        int read = inputStream.read();
        if (read > 0) {
            int value = Tools.byteArray2Int(buffer);
            System.out.println("收到数据：" + read + " 数据：" + value);
        } else {
            System.out.println("没有收到数据：" + read);
        }
         */

        outputStream.close();
        inputStream.close();
    }

    private static void initSocket(Socket socket) throws SocketException {
        // 设置读取超时时间为2s
        socket.setSoTimeout(2000);

        // 是否复用未完全关闭的Socket地址，对于指定bind操作后的套接字有效
        socket.setReuseAddress(true);

        // 是否开启Nagle算法
        socket.setTcpNoDelay(true);

        // 是否需要再长时间无数据响应时发送确认数据（类似心跳包），时间大约为2h
        socket.setKeepAlive(true);

        // 对于close关闭操作行为进行怎样的处理；默认为false 0
        // false 0：默认设置，关闭时立即返回，底层系统接管输出流，将缓冲区内的数据发送完成
        // true 0：关闭时立即返回，缓冲区数据抛弃，直接发送RST结束命令到对方，并无需经过2MSL等待
        // true 200：关闭时最长阻塞200毫秒，随后按第二种情况处理
        socket.setSoLinger(true, 200);

        // 是否让紧急数据内敛，默认false；紧急数据通过socket.sendUrgentData(1)发送
        socket.setOOBInline(false);

        // 设置接收/发送缓冲器大小
        socket.setReceiveBufferSize(64 * 1024 * 1024);
        socket.setSendBufferSize(64 * 1024 * 1024);

        // 设置性能参数：短链接、延迟、带宽的相对重要性
        socket.setPerformancePreferences(1, 1, 1);
    }

    private static Socket createSocket() throws IOException {

        /**
         // 无代理模式，等同于空构造函数
         Socket socket = new Socket(Proxy.NO_PROXY);

         // 新建一份具有HTTP代理的套接字，传输数据将通过www.baidu.com:8888端口转发
         Proxy proxy = new Proxy(Proxy.Type.HTTP,
         new InetSocketAddress(Inet4Address.getByName("www.baidu.com"), 8888));
         socket = new Socket(proxy);

         // 新建一个套接字，并直接链到本地20000的服务器上
         socket = new Socket("localhost", PORT);

         // 新建一个套接字，并直接链到本地20000的服务器上
         socket = new Socket(Inet4Address.getLocalHost(), PORT);

         // 新建一个套接字，并直接链到本地20000的服务器上，并绑定到本地20001端口上
         socket = new Socket("localhost", PORT, Inet4Address.getLocalHost(), LOCAL_PORT);
         socket = new Socket(Inet4Address.getLocalHost(), PORT, Inet4Address.getLocalHost(), LOCAL_PORT);
         */

        // 绑定本地20001端口
        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(InetAddress.getLocalHost(), LOCAL_PORT));

        return socket;
    }

}
