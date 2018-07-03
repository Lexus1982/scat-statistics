package me.alexand.scat.statistic.collector.network.impl;

import me.alexand.scat.statistic.collector.model.RawDataPacket;
import me.alexand.scat.statistic.collector.network.PacketsReceiver;
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
 * <p>
 * В отдельном потоке получает UDP-пакеты на указанном сокете
 * и сохраняет во внутреннем буфере.
 *
 * @author asidorov84@gmail.com
 */

@Repository
public class UDPPacketsReceiverImpl implements PacketsReceiver, Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(UDPPacketsReceiverImpl.class);
    private static final int MAX_UDP_PACKET_SIZE = 65535;

    private DatagramSocket socket;
    private final byte[] buffer = new byte[MAX_UDP_PACKET_SIZE];
    private final BlockingQueue<RawDataPacket> packetsBuffer;
    private final Thread thread;
    private final StatCollector statCollector;
    private boolean shutdownFlag = false;

    public UDPPacketsReceiverImpl(@Value("${packet.buffer.capacity}") int bufferCapacity,
                                  @Value("${net.address}") String listenAddress,
                                  @Value("${net.port}") int listenPort,
                                  StatCollector statCollector) {
        this.statCollector = statCollector;
        LOGGER.info("Initialize packets buffer with capacity: {}", bufferCapacity);
        packetsBuffer = new ArrayBlockingQueue<>(bufferCapacity);

        try {
            socket = new DatagramSocket(listenPort, InetAddress.getByName(listenAddress));
            socket.setReceiveBufferSize(MAX_UDP_PACKET_SIZE);

            LOGGER.info("Start listening on {}:{}",
                    socket.getLocalAddress().getHostAddress(),
                    socket.getLocalPort());

        } catch (SocketException | UnknownHostException e) {
            LOGGER.error("Critical error while initializing socket: {}", e.toString());
            LOGGER.error("Exit with status 1");
            System.exit(1);
        }

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
            while (!thread.isInterrupted()) {
                RawDataPacket packet = receive();
                statCollector.registerReceivedPacket();

                if (!packetsBuffer.offer(packet)) {
                    statCollector.registerInputBufferOverflow();
                }
            }
        } catch (IOException e) {
            if (!shutdownFlag) {
                LOGGER.error("Critical error while receiving packets: {}", e.toString());
                LOGGER.error("Exit with status 2");
                System.exit(1);
            }
        }
    }

    private RawDataPacket receive() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return RawDataPacket.builder()
                .address(packet.getAddress())
                .port(packet.getPort())
                .pdu(copyOf(buffer, packet.getLength()))
                .build();
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Stop receiving packets and close the socket");
        shutdownFlag = true;
        socket.close();
    }
}