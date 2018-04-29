package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.RawDataPacket;
import me.alexand.scat.statistic.collector.repository.PacketsReceiver;
import me.alexand.scat.statistic.collector.service.StatCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.Arrays.copyOf;

/**
 * Приемник UDP-пакетов
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
    private final StatCollector statCollector;

    private DatagramSocket socket;
    private Thread thread;

    public UDPPacketsReceiverImpl(@Value("${packet.buffer.capacity}") int bufferCapacity,
                                  @Value("${net.address}") String listenAddress,
                                  @Value("${net.port}") int listenPort,
                                  @Value("${net.socket.timeout}") int socketTimeout,
                                  @Value("${net.socket.buffer.length}") int socketBufferLength,
                                  StatCollector statCollector) {
        this.listenAddress = listenAddress;
        this.listenPort = listenPort;
        this.socketTimeout = socketTimeout;
        this.socketBufferLength = socketBufferLength;
        this.statCollector = statCollector;
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
            init();

            while (!thread.isInterrupted()) {
                try {
                    RawDataPacket packet = receive();
                    statCollector.registerReceivedPacket();
                    if (!packetsBuffer.offer(packet)) {
                        statCollector.registerInputBufferOverflow();
                    }
                } catch (SocketTimeoutException ste) {
                    LOGGER.error("socket timeout: {}", ste.toString());
                }
            }
        } catch (UnknownHostException uhe) {
            LOGGER.error("invalid listen address: {}", uhe.toString());
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }

    private void init() throws SocketException, UnknownHostException {
        socket = new DatagramSocket(listenPort, InetAddress.getByName(listenAddress));

        socket.setReceiveBufferSize(socketBufferLength);
        socket.setSoTimeout(socketTimeout * 1000);

        LOGGER.info("start listening on {}:{}", socket.getLocalAddress().getHostAddress(), socket.getLocalPort());
    }

    public RawDataPacket receive() throws IOException {
        byte[] buffer = new byte[65535];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);

        return new RawDataPacket(copyOf(buffer, packet.getLength()));
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("stop receiving packets");
        thread.interrupt();

        try {
            thread.join();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        } finally {
            LOGGER.info("closing socket");
            socket.close();
        }
    }
}