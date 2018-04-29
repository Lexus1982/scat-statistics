package me.alexand.scat.statistic.collector.model;

import java.util.List;
import java.util.Objects;

/**
 * IP Flow Information Export (IPFIX) Template Record
 * -----------------------------------------------------------------
 * +0                   1                   2                   3
 * +0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       Template ID (> 255)     |           Field Count         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        Field specifier                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                             ...                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        Field specifier                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @author asidorov84@gmail.com
 * @link https://tools.ietf.org/html/rfc7011
 */

public class IPFIXTemplateRecord extends AbstractIPFIXRecord {
    private final int templateID;
    private final int fieldCount;
    private final long exportTime;
    private final TemplateType type;
    private final List<IPFIXFieldSpecifier> fieldSpecifiers;

    public static IPFIXTemplateRecord.Builder builder() {
        return new IPFIXTemplateRecord.Builder();
    }

    private IPFIXTemplateRecord(IPFIXTemplateRecord.Builder builder) {
        this.templateID = builder.templateID;
        this.fieldCount = builder.fieldCount;
        this.exportTime = builder.exportTime;
        this.type = builder.type;
        this.fieldSpecifiers = builder.fieldSpecifiers;
    }

    public int getTemplateID() {
        return templateID;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public long getExportTime() {
        return exportTime;
    }

    public TemplateType getType() {
        return type;
    }

    public List<IPFIXFieldSpecifier> getFieldSpecifiers() {
        return fieldSpecifiers;
    }

    public int getMinDataRecordSize() {
        return fieldSpecifiers.stream()
                .filter(s -> s.getFieldLength() != 65535)
                .map(IPFIXFieldSpecifier::getFieldLength)
                .reduce(0, Integer::sum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXTemplateRecord that = (IPFIXTemplateRecord) o;
        return templateID == that.templateID &&
                fieldCount == that.fieldCount &&
                exportTime == that.exportTime &&
                type == that.type &&
                Objects.equals(fieldSpecifiers, that.fieldSpecifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateID, fieldCount, exportTime, type, fieldSpecifiers);
    }

    @Override
    public String toString() {
        return "IPFIXTemplateRecord{" +
                "templateID=" + templateID +
                ", fieldCount=" + fieldCount +
                ", exportTime=" + exportTime +
                ", type=" + type +
                ", fieldSpecifiers=" + fieldSpecifiers +
                '}';
    }

    public static class Builder {
        private int templateID;
        private int fieldCount;
        private long exportTime;
        private TemplateType type;
        private List<IPFIXFieldSpecifier> fieldSpecifiers;

        private Builder() {
        }

        public Builder templateID(int templateID) {
            this.templateID = templateID;
            return this;
        }

        public Builder fieldCount(int fieldCount) {
            this.fieldCount = fieldCount;
            return this;
        }

        public Builder exportTime(long exportTime) {
            this.exportTime = exportTime;
            return this;
        }

        public Builder type(TemplateType type) {
            this.type = type;
            return this;
        }

        public Builder fieldSpecifiers(List<IPFIXFieldSpecifier> fieldSpecifiers) {
            this.fieldSpecifiers = fieldSpecifiers;
            return this;
        }

        public IPFIXTemplateRecord build() {
            return new IPFIXTemplateRecord(this);
        }
    }
}
