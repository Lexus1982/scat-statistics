package me.alexand.scat.statistic.collector.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * IP Flow Information Export (IPFIX) message Set
 * -----------------------------------------------------------------
 * +0                   1                   2                   3
 * +0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |              Set ID           |            Length             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                             record                            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                             record                            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                             record                            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       padding (optional)                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @author asidorov84@gmail.com
 * @link https://tools.ietf.org/html/rfc7011
 */

public class IPFIXSet {
    private final int setID;
    private final int length;
    private final List<AbstractIPFIXRecord> records;

    public static IPFIXSet.Builder builder() {
        return new IPFIXSet.Builder();
    }

    private IPFIXSet(IPFIXSet.Builder builder) {
        this.setID = builder.setID;
        this.length = builder.length;
        this.records = builder.records;
    }

    public int getSetID() {
        return setID;
    }

    public int getLength() {
        return length;
    }

    public List<AbstractIPFIXRecord> getRecords() {
        return records;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXSet ipfixSet = (IPFIXSet) o;
        return setID == ipfixSet.setID &&
                length == ipfixSet.length &&
                Objects.equals(records, ipfixSet.records);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setID, length, records);
    }

    @Override
    public String toString() {
        return "IPFIXSet{" +
                "setID=" + setID +
                ", length=" + length +
                ", records=" + records +
                '}';
    }

    public static class Builder {
        private int setID;
        private int length;
        private List<AbstractIPFIXRecord> records = new ArrayList<>();

        private Builder() {
        }

        public Builder setID(int setID) {
            this.setID = setID;
            return this;
        }

        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public Builder records(List<AbstractIPFIXRecord> records) {
            this.records = records;
            return this;
        }

        public IPFIXSet build() {
            return new IPFIXSet(this);
        }
    }
}