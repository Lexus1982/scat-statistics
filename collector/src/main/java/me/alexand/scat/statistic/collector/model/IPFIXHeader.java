package me.alexand.scat.statistic.collector.model;

import java.util.Objects;

/**
 * IP Flow Information Export (IPFIX) message header
 * -----------------------------------------------------------------
 * +0                   1                   2                   3
 * +0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Version Number        |            Length             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         Export Time                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Sequence Number                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                   Observation Domain ID                       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @author asidorov84@gmail.com
 * @link https://tools.ietf.org/html/rfc7011
 */

public class IPFIXHeader {
    private final int version;
    private final int length;
    private final long exportTime;
    private final long sequenceNumber;
    private final long observationDomainID;

    public static IPFIXHeader.Builder builder() {
        return new IPFIXHeader.Builder();
    }

    private IPFIXHeader(IPFIXHeader.Builder builder) {
        this.version = builder.version;
        this.length = builder.length;
        this.exportTime = builder.exportTime;
        this.sequenceNumber = builder.sequenceNumber;
        this.observationDomainID = builder.observationDomainID;
    }

    public int getVersion() {
        return version;
    }

    public int getLength() {
        return length;
    }

    public long getExportTime() {
        return exportTime;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public long getObservationDomainID() {
        return observationDomainID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXHeader that = (IPFIXHeader) o;
        return version == that.version &&
                length == that.length &&
                exportTime == that.exportTime &&
                sequenceNumber == that.sequenceNumber &&
                observationDomainID == that.observationDomainID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, length, exportTime, sequenceNumber, observationDomainID);
    }

    @Override
    public String toString() {
        return "IPFIXHeader{" +
                "version=" + version +
                ", length=" + length +
                ", exportTime=" + exportTime +
                ", sequenceNumber=" + sequenceNumber +
                ", observationDomainID=" + observationDomainID +
                '}';
    }

    public static class Builder {
        private int version;
        private int length;
        private long exportTime;
        private long sequenceNumber;
        private long observationDomainID;

        private Builder() {
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public Builder exportTime(long exportTime) {
            this.exportTime = exportTime;
            return this;
        }

        public Builder sequenceNumber(long sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public Builder observationDomainID(long observationDomainID) {
            this.observationDomainID = observationDomainID;
            return this;
        }

        public IPFIXHeader build() {
            return new IPFIXHeader(this);
        }
    }
}