package me.alexand.scat.statistic.collector.network.impl;

import me.alexand.scat.statistic.collector.model.RawDataPacket;
import me.alexand.scat.statistic.collector.network.PacketsReceiver;
import me.alexand.scat.statistic.collector.service.StatCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.copyOf;

/**
 * Приемник UDP-пакетов
 * <p>
 * В отдельном потоке получает UDP-пакеты на указанном сокете
 * и сохраняет во внутреннем буфере.
 *
 * @author asidorov84@gmail.com
 */

@Component
public class UDPPacketsReceiverImpl implements PacketsReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(UDPPacketsReceiverImpl.class);
    private static final int UDP_RECEIVE_BUFFER_SIZE = 33_554_432;
    private static final int RECEIVERS_COUNT = 16;

    private DatagramSocket socket;
    private final BlockingQueue<RawDataPacket> packetsBuffer;
    private final ExecutorService receiversPool;
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
            socket.setReceiveBufferSize(UDP_RECEIVE_BUFFER_SIZE);

            LOGGER.info("Start listening on {}:{}",
                    socket.getLocalAddress().getHostAddress(),
                    socket.getLocalPort());

        } catch (SocketException | UnknownHostException e) {
            LOGGER.error("Critical error while initializing socket: {}", e.toString());
            LOGGER.error("Exit with status 1");
            System.exit(1);
        }

        LOGGER.info("Initializing {} receivers", RECEIVERS_COUNT);
        receiversPool = Executors.newFixedThreadPool(RECEIVERS_COUNT);
        for (int i = 0; i < RECEIVERS_COUNT; i++) {
            receiversPool.submit(new Receiver());
        }
    }

    @Override
    public RawDataPacket getNextPacket() throws InterruptedException {
        return packetsBuffer.take();
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Stop receiving packets and close the socket");
        shutdownFlag = true;

        try {
            receiversPool.shutdownNow();

            LOGGER.info("...waiting until all receivers stopped");

            while (!receiversPool.isTerminated()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            LOGGER.info("Normal shutdown  receivers failed");
        }

        socket.close();
    }

    private class Receiver implements Runnable {
        private final byte[] buffer = new byte[65535];

        @Override
        public void run() {
            try {
                while (true) {
                    RawDataPacket packet = receive();

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
            statCollector.registerReceivedPacket();
            return RawDataPacket.builder()
                    .address(packet.getAddress())
                    .port(packet.getPort())
                    .pdu(copyOf(buffer, packet.getLength()))
                    .build();
        }
    }
}