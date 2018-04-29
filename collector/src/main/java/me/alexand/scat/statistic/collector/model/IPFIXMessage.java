package me.alexand.scat.statistic.collector.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * IP Flow Information Export (IPFIX) message
 * +----------------------------------------------------+
 * | Message Header                                     |
 * +----------------------------------------------------+
 * | Set                                                |
 * +----------------------------------------------------+
 * | Set                                                |
 * +----------------------------------------------------+
 * ...
 * +----------------------------------------------------+
 * | Set                                                |
 * +----------------------------------------------------+
 *
 * @author asidorov84@gmail.com
 * @link https://tools.ietf.org/html/rfc7011
 */
public class IPFIXMessage {
    private final IPFIXHeader header;
    private final List<IPFIXSet> sets;

    public static IPFIXMessage.Builder builder() {
        return new IPFIXMessage.Builder();
    }

    private IPFIXMessage(IPFIXMessage.Builder builder) {
        this.header = builder.header;
        this.sets = builder.sets;
    }

    public IPFIXHeader getHeader() {
        return header;
    }

    public List<IPFIXSet> getSets() {
        return sets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXMessage that = (IPFIXMessage) o;
        return Objects.equals(header, that.header) &&
                Objects.equals(sets, that.sets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, sets);
    }

    @Override
    public String toString() {
        return "IPFIXMessage{" +
                "header=" + header +
                ", sets=" + sets +
                '}';
    }

    public static class Builder {
        private IPFIXHeader header;
        private List<IPFIXSet> sets = new ArrayList<>();

        private Builder() {
        }

        public Builder header(IPFIXHeader header) {
            this.header = header;
            return this;
        }

        public Builder sets(List<IPFIXSet> sets) {
            this.sets = sets;
            return this;
        }

        public IPFIXMessage build() {
            return new IPFIXMessage(this);
        }
    }
}