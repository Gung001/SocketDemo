package com.lxgy.udp;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * UDP 搜索者，用于搜索服务提供方
 *
 * @author Gryant
 */
public class UDPSearcher {

    public static final int LISTEN_PORT = 30000;

    private static Listener listen() throws IOException, InterruptedException {

        System.out.println("UDPSearcher started listener.");

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();

        countDownLatch.await();

        System.out.println("UDPSearcher finished listener.");
        return listener;
    }

    private static void sendBroadcast() throws IOException {
        System.out.println("UDPSearcher sendBroadcast started.");

        // 作为一个搜索者，让系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        // 构建一份要发送的数据
        String requestData = MessageCreator.buildWithPort(LISTEN_PORT);
        byte[] requestDataBytes = requestData.getBytes();
        // 直接根据发送者构建一份回送数据
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes, requestDataBytes.length);
        // 端口20000，广播地址
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPacket.setPort(20000);

        // 发送
        ds.send(requestPacket);
        ds.close();
        System.out.println("UDPSearcher sendBroadcast finished.");
    }

    /**
     * 监听线程
     */
    private static class Listener extends Thread {

        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private List<Device> devices = new ArrayList<>();
        private boolean done = false;
        private DatagramSocket ds = null;

        private Listener(int listenPort, CountDownLatch countDownLatch) {
            super();
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();

            // 通知已启动
            countDownLatch.countDown();

            try {

                // 监听回送端口
                ds = new DatagramSocket(listenPort);

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
                    System.out.println("UDPSearcher Listener receive from" +
                            "，ip:" + ip +
                            "，port:" + port +
                            "，data:" + data);

                    String sn = MessageCreator.parseSn(data);
                    if (sn != null) {
                        devices.add(new Device(port, ip, sn));
                    }
                }
            } catch (Exception ignored) {
            } finally {
                exit();
            }

            System.out.println("UDPSearcher Listener finished.");
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

        List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }
    }

    private static class Device {
        final int port;
        final String ip;
        final String sn;

        private Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip=" + ip +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }

    /**
     * v2.0
     *
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher started.");


        Listener listener = listen();
        sendBroadcast();

        // 读取任意键盘都可以退出
        System.in.read();

        List<Device> devices = listener.getDevicesAndClose();

        devices.forEach(e -> System.out.println(e));

        System.out.println("UDPSearcher finished.");
    }

    /**
     * v1.0
     *
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main1(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher started.");

        // 作为一个搜索者，让系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        // 构建一份要发送的数据
        String requestData = "Hello World";
        byte[] requestDataBytes = requestData.getBytes();
        // 直接根据发送者构建一份回送数据
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes, requestDataBytes.length);
        requestPacket.setAddress(InetAddress.getLocalHost());
        requestPacket.setPort(20000);

        // 发送
        ds.send(requestPacket);

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
        System.out.println("UDPSearcher receive from" +
                "\nip:" + ip +
                "\nport:" + port +
                "\ndata:" + data);


        ds.close();
        System.out.println("UDPSearcher finished.");
    }

}
