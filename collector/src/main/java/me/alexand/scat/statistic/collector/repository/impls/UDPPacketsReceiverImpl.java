package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.RawDataPacket;
import me.alexand.scat.statistic.collector.repository.PacketsReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.lang.System.arraycopy;

/**
 * Приемник UDP-пакетов
 *
 * @author asidorov84@gmail.com
 */

@Repository
public class UDPPacketsReceiverImpl implements PacketsReceiver, Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(UDPPacketsReceiverImpl.class);

    private final BlockingQueue<RawDataPacket> packetsBuffer;
    private final String listenAddress;
    private final int listenPort;
    private final int socketTimeout;
    private final int socketBufferLength;

    private DatagramSocket socket;
    private Thread thread;

    public UDPPacketsReceiverImpl(@Value("${packet.buffer.capacity}") int bufferCapacity,
                                  @Value("${net.address}") String listenAddress,
                                  @Value("${net.port}") int listenPort,
                                  @Value("${net.socket.timeout}") int socketTimeout,
                                  @Value("${net.socket.buffer.length}") int socketBufferLength) {
        this.listenAddress = listenAddress;
        this.listenPort = listenPort;
        this.socketTimeout = socketTimeout;
        this.socketBufferLength = socketBufferLength;
        packetsBuffer = new ArrayBlockingQueue<>(bufferCapacity);

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public RawDataPacket getNextPacket() throws InterruptedException {
        return packetsBuffer.take();
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(listenPort, InetAddress.getByName(listenAddress));

            socket.setReceiveBufferSize(socketBufferLength);
            socket.setSoTimeout(socketTimeout * 1000);

            LOGGER.info("start listening on {}:{}", socket.getLocalAddress().getHostAddress(), socket.getLocalPort());

            byte[] buffer = new byte[65535];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (!thread.isInterrupted()) {
                try {
                    socket.receive(packet);

                    byte[] payload = new byte[packet.getLength()];
                    arraycopy(packet.getData(), 0, payload, 0, packet.getLength());

                    packetsBuffer.put(new RawDataPacket(payload));
                } catch (SocketTimeoutException ste) {
                    LOGGER.error("socket timeout: {}", ste.toString());
                }
            }
        } catch (UnknownHostException uhe) {
            LOGGER.error("invalid listen address: {}", uhe.toString());
        } catch (Exception e) {
            LOGGER.error(e.toString());
        } finally {
            LOGGER.info("closing socket");
            socket.close();
        }
    }

    @PreDestroy
    private void shutdown() {
        thread.interrupt();

        try {
            thread.join();
        } catch (InterruptedException e) {
            return;
        }

        LOGGER.info("stop receiving packets");
    }
}