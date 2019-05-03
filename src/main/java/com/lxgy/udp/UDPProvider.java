package com.lxgy.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * UDP 提供者，用于提供服务
 *
 * @author Gryant
 */
public class UDPProvider {

    /**
     * v2.0
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        System.out.println("UDPProvider started.");

        // 生成一份唯一标识
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        provider.start();

        // 读取任意键盘信息后可以退出
        System.in.read();
        provider.close();

        System.out.println("UDPProvider finished.");
    }

    /**
     * v1.0
     *
     * @param args
     * @throws IOException
     */
    public static void main1(String[] args) throws IOException {

        System.out.println("UDPProvider started.");

        // 作为一个接收者，指定一个端口用于数据接收
        DatagramSocket ds = new DatagramSocket(20000);

        // 构建接收实体
        final byte[] buf = new byte[512];
        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

        // 接收
        ds.receive(receivePacket);

        // 打印接收到的信息与发送者的信息
        // 发送者IP地址
        String ip = receivePacket.getAddress().getHostAddress();
        int port = receivePacket.getPort();
        int dataLength = receivePacket.getLength();
        String data = new String(receivePacket.getData(), 0, dataLength);
        System.out.println("UDPProvider receive from" +
                "\nip:" + ip +
                "\nport:" + port +
                "\ndata:" + data);

        // 构建一份回送数据
        String responseData = "Receive data with len:" + dataLength;
        byte[] responseDataBytes = responseData.getBytes();
        // 直接根据发送者构建一份回送数据
        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                responseDataBytes.length,
                receivePacket.getAddress(),
                receivePacket.getPort());

        ds.send(responsePacket);

        ds.close();
        System.out.println("UDPProvider finished.");
    }

    /**
     * 提供者线程
     */
    private static class Provider extends Thread {

        private final String sn;
        private Boolean done = false;
        private DatagramSocket ds = null;

        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();

            System.out.println("UDPProvider started.");

            try {

                // 监听20000端口
                ds = new DatagramSocket(20000);

                while (!done) {

                    // 构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

                    // 接收
                    ds.receive(receivePacket);

                    // 打印接收到的信息与发送者的信息
                    // 发送者IP地址
                    String ip = receivePacket.getAddress().getHostAddress();
                    int port = receivePacket.getPort();
                    int dataLength = receivePacket.getLength();
                    String data = new String(receivePacket.getData(), 0, dataLength);
                    System.out.println("UDPProvider receive from" +
                            "\nip:" + ip +
                            "\nport:" + port +
                            "\ndata:" + data);

                    // 解析端口号
                    int responsePort = MessageCreator.parsePort(data);
                    if (responsePort != -1) {
                        // 构建一份回送数据
                        String responseData = MessageCreator.buildWithSn(sn);
                        byte[] responseDataBytes = responseData.getBytes();
                        // 直接根据发送者构建一份回送数据
                        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                                responseDataBytes.length,
                                receivePacket.getAddress(),
                                responsePort);

                        ds.send(responsePacket);
                    }
                }
            } catch (Exception ignored) {
            } finally {
                exit();
            }
            System.out.println("UDPProvider finished.");
        }

        void exit() {
            done = true;
            close();
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }
    }
}
