package me.alexand.scat.statistic.collector.network;

import me.alexand.scat.statistic.collector.model.IPFIXHeader;
import me.alexand.scat.statistic.collector.service.IPFIXParser;
import me.alexand.scat.statistic.collector.service.StatCollector;
import me.alexand.scat.statistic.collector.utils.exceptions.IPFIXParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.MAX_PRIORITY;
import static java.util.Arrays.copyOf;
import static me.alexand.scat.statistic.collector.utils.Constants.MESSAGE_HEADER_LENGTH;

/**
 * @author asidorov84@gmail.com
 */
@Component("TCPPacketsReceiver")
@Lazy
public final class TCPPacketsReceiver implements PacketsReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(TCPPacketsReceiver.class);
    private final BlockingQueue<byte[]> packetsBuffer;

    private final ServerSocket serverSocket;
    private final Thread listenerThread;

    private final StatCollector statCollector;
    private final IPFIXParser parser;

    public TCPPacketsReceiver(@Value("${net.address}") String listenAddress,
                              @Value("${net.port}") int listenPort,
                              @Value("${packet.buffer.capacity}") int bufferCapacity,
                              @Value("${socket.receive.buffer.size}") int socketReceiveBufferSize,
                              StatCollector statCollector,
                              IPFIXParser parser) throws IOException {
        InetAddress address = InetAddress.getByName(listenAddress);

        if (listenPort <= 1024 || listenPort >= 65535) {
            throw new IllegalArgumentException("illegal port number");
        }

        if (bufferCapacity <= 0) {
            throw new IllegalArgumentException("illegal size of buffer");
        }

        if (socketReceiveBufferSize <= 0) {
            throw new IllegalArgumentException("illegal SO_RCVBUF size");
        }

        this.statCollector = statCollector;
        this.parser = parser;

        serverSocket = new ServerSocket(listenPort, 10, address);
        serverSocket.setReceiveBufferSize(socketReceiveBufferSize);

        LOGGER.info("Creating socket on {}:{}",
                serverSocket.getInetAddress().getHostAddress(),
                serverSocket.getLocalPort());

        packetsBuffer = new ArrayBlockingQueue<>(bufferCapacity);
        LOGGER.info("Initialize internal packets buffer with size: {}", bufferCapacity);

        listenerThread = new Thread(new Listener(), "tcp-listener-thread");
        listenerThread.setPriority(MAX_PRIORITY);
        listenerThread.start();
    }

    @Override
    public byte[] getNextPacket() throws InterruptedException {
        return packetsBuffer.take();
    }

    //TODO нужен метод shutdown для остановки потоков и закрытия всех сокетов

    private class Listener implements Runnable {
        @Override
        public void run() {
            int sessionsCounter = 1;
            LOGGER.info("Start listening for incoming connections...");

            try {
                while (!listenerThread.isInterrupted()) {
                    Socket sessionSocket = serverSocket.accept();

                    LOGGER.info("Got connection from {}:{}",
                            sessionSocket.getInetAddress().getHostAddress(),
                            sessionSocket.getPort());

                    LOGGER.info("Start new TCP-session with id = {}", sessionsCounter);
                    LOGGER.info("Receive buffer size of socket: {}", sessionSocket.getReceiveBufferSize());

                    TCPSession session = new TCPSession(sessionSocket, sessionsCounter);
                    sessionsCounter++;

                    new Thread(session, String.format("tcp-session-%d-thread", sessionsCounter)).start();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }

            LOGGER.info("Stop listening for incoming connections...");
        }
    }

    private class TCPSession implements Runnable {
        private final Socket socket;
        private final int id;
        private final byte[] buffer = new byte[65535];
        private final byte[] rawHeader = new byte[MESSAGE_HEADER_LENGTH];

        public TCPSession(Socket socket, int id) {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run() {
            LOGGER.debug("Start receiving packets within new session (id = {})...", id);

            try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                IPFIXHeader header = null;

                while (!Thread.currentThread().isInterrupted()) {
                    //читаем следующие 16 байт заголовка
                    dis.readFully(rawHeader, 0, rawHeader.length);
                    System.arraycopy(rawHeader, 0, buffer, 0, rawHeader.length);

                    try {
                        header = parser.parseHeader(rawHeader);
                    } catch (IPFIXParseException e) {
                        LOGGER.error(e.getMessage());
                        break;
                    }

                    int messageLength = header.getLength();

                    //Читаем остаток сообщения
                    dis.readFully(buffer, MESSAGE_HEADER_LENGTH, messageLength - MESSAGE_HEADER_LENGTH);

                    if (!packetsBuffer.offer(copyOf(buffer, messageLength))) {
                        statCollector.registerInputBufferOverflow();
                    }
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info("Stop receiving packets within new session (id = {})...", id);
        }
    }
}
