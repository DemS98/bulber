package com.demetrio.bulber.engine;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import com.demetrio.bulber.engine.bulb.Bulb;
import com.demetrio.bulber.engine.bulb.Request;
import com.demetrio.bulber.engine.bulb.System;
import com.demetrio.bulber.view.Device;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class KasaManager {

    private static final Logger logger = LoggerFactory.getLogger(KasaManager.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final BulberProperties props = BulberProperties.getInstance();

    private static class BulbConnection implements Closeable {
        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        public BulbConnection(Socket socket) throws IOException {
            this.socket = socket;
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public DataInputStream getDataInputStream() {
            return dataInputStream;
        }

        public void setDataInputStream(DataInputStream dataInputStream) {
            this.dataInputStream = dataInputStream;
        }

        public DataOutputStream getDataOutputStream() {
            return dataOutputStream;
        }

        public void setDataOutputStream(DataOutputStream dataOutputStream) {
            this.dataOutputStream = dataOutputStream;
        }

        @Override
        public void close() throws IOException {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        }

        public void reconnect() throws IOException {
            close();
            socket = new Socket(socket.getInetAddress(), socket.getPort());
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }
    }

    private static BulbConnection bulbConnection;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (bulbConnection != null) {
                try {
                    bulbConnection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private KasaManager() {}

    private static List<InetAddress> getBroadcastAddresses() throws SocketException {
        List<InetAddress> addresses = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(InterfaceAddress::getBroadcast)
                    .filter(Objects::nonNull)
                    .forEach(addresses::add);
        }

        return addresses;
    }

    public static List<Device> getDevices() throws IOException {
        if (bulbConnection != null) {
            bulbConnection.close();
            bulbConnection = null;
        }

        try (DatagramSocket broadcastSocket = new DatagramSocket()) {
            broadcastSocket.setBroadcast(true);
            broadcastSocket.setSoTimeout((int)TimeUnit.SECONDS.toMillis(3));

            byte[] requestContent = encodeTPLinkFormat(mapper.writeValueAsBytes(new Bulb(new System())));
            List<InetAddress> addresses = getBroadcastAddresses();
            ExecutorService executorService = Executors.newFixedThreadPool(addresses.size());

            logger.info(props.getProperty(BulberConst.BROADCAST_START));
            addresses.forEach(address -> executorService.execute(() -> {
                DatagramPacket packet = new DatagramPacket(requestContent, requestContent.length, address, 9999);
                try {
                    broadcastSocket.send(packet);
                } catch (IOException e) {
                    logger.error(props.getProperty(BulberConst.BROADCAST_ERROR), e);
                }
            }));
            executorService.shutdown();

            List<Device> devices = new ArrayList<>();
            byte[] buf = new byte[64 * 1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                while (true) {
                    broadcastSocket.receive(packet);
                    try {
                        Bulb bulb = mapper.readValue(decodeTPLinkFormat(packet.getData(), 0, packet.getLength()), Bulb.class);
                        devices.add(new Device(packet.getAddress().getHostAddress(), bulb));
                    } catch (JsonProcessingException e) {
                        // if not bulb
                        logger.warn(props.getProperty(BulberConst.NON_BULB_DEVICE));
                    }
                }
            } catch (SocketTimeoutException e) {
                logger.info(props.getProperty(BulberConst.BROADCAST_END));
            }
            return devices;
        }
    }

    public static String execute(Device device, Request request) throws IOException {
        if (bulbConnection == null) {
            bulbConnection = new BulbConnection(new Socket(device.getAddress(), 9999));
        }

        while (true) {
            byte[] content = encodeTPLinkFormat(mapper.writeValueAsBytes(request));
            try {
                bulbConnection.getDataOutputStream().writeInt(content.length);
                bulbConnection.getDataOutputStream().write(content);
                bulbConnection.getDataOutputStream().flush();

                byte[] response = new byte[bulbConnection.getDataInputStream().readInt()];
                bulbConnection.getDataInputStream().readFully(response);
                return new String(decodeTPLinkFormat(response, 0, response.length), StandardCharsets.UTF_8);
            } catch (Exception e) {
                logger.error(props.getProperty(BulberConst.NETWORK_ERROR), e);
                bulbConnection.reconnect();
            }
        }
    }

    private static byte[] encodeTPLinkFormat(byte[] jsonContent) {
        byte[] buf = new byte[jsonContent.length];
        byte key = (byte) 0xAB;
        for(int i=0; i < jsonContent.length; i++) {
            buf[i] = (byte) (key ^ jsonContent[i]);
            key = buf[i];
        }
        return buf;
    }

    private static byte[] decodeTPLinkFormat(byte[] content, int offset, int length) {
        byte[] buf = new byte[length];
        byte key = (byte) 0xAB;
        for(int i=0; i < length; i++) {
            buf[i] = (byte) (key ^ content[i + offset]);
            key = content[i + offset];
        }
        return buf;
    }

}
