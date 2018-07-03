package me.alexand.scat.statistic.collector.model;

import java.net.InetAddress;

/**
 * Необработанный пакет данных
 *
 * @author asidorov84@gmail.com
 */
public class RawDataPacket {
    private final InetAddress address;
    private final int port;
    private final byte[] pdu;

    private RawDataPacket(RawDataPacket.Builder builder) {
        this.address = builder.address;
        this.port = builder.port;
        this.pdu = builder.pdu;
    }

    public static RawDataPacket.Builder builder() {
        return new RawDataPacket.Builder();
    }

    public byte[] getPdu() {
        return pdu;
    }

    public int getLength() {
        return pdu.length;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public static class Builder {
        private InetAddress address;
        private int port;
        private byte[] pdu;

        private Builder() {
        }

        public Builder address(InetAddress address) {
            this.address = address;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder pdu(byte[] pdu) {
            this.pdu = pdu;
            return this;
        }

        public RawDataPacket build() {
            return new RawDataPacket(this);
        }
    }
}