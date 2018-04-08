package me.alexand.scat.statistic.collector.model;

import java.util.Objects;

/**
 * IPFIX Field Specifier
 * +0                   1                   2                   3
 * +0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |E|  Information Element ident. |          Field Length         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                     Enterprise Number                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @author asidorov84@gmail.com
 * @link https://tools.ietf.org/html/rfc7011
 */

public class IPFIXFieldSpecifier {

    //If E bit is zero, the Information Element identifier identifies an Information
    //Element in IANA-IPFIX, and the 4-bytes Enterprise Number field must not present.
    //If E bit is one, the Information Element identifier identifies an enterprise-
    //specific Information Element, and the Enterprise Number field must be present.
    private final boolean enterpriseBit;

    //A numeric value that represents the Information Element.
    private final int informationElementIdentifier;

    //The length of the corresponding encoded Information Element, in bytes.
    private final int fieldLength;

    //IANA enterprise number of the authority defining the Information Element identifier
    //in the IPFIX Template Record.
    private final long enterpriseNumber;

    private IPFIXFieldSpecifier(IPFIXFieldSpecifier.Builder builder) {
        this.enterpriseBit = builder.enterpriseBit;
        this.informationElementIdentifier = builder.informationElementIdentifier;
        this.fieldLength = builder.fieldLength;
        this.enterpriseNumber = builder.enterpriseNumber;
    }

    public static IPFIXFieldSpecifier.Builder builder() {
        return new IPFIXFieldSpecifier.Builder();
    }

    public boolean isEnterpriseBit() {
        return enterpriseBit;
    }

    public int getInformationElementIdentifier() {
        return informationElementIdentifier;
    }

    public int getFieldLength() {
        return fieldLength;
    }

    public long getEnterpriseNumber() {
        return enterpriseNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXFieldSpecifier that = (IPFIXFieldSpecifier) o;
        return enterpriseBit == that.enterpriseBit &&
                informationElementIdentifier == that.informationElementIdentifier &&
                fieldLength == that.fieldLength &&
                enterpriseNumber == that.enterpriseNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enterpriseBit, informationElementIdentifier, fieldLength, enterpriseNumber);
    }

    @Override
    public String toString() {
        return "IPFIXFieldSpecifier{" +
                "enterpriseBit=" + enterpriseBit +
                ", informationElementIdentifier=" + informationElementIdentifier +
                ", fieldLength=" + fieldLength +
                ", enterpriseNumber=" + enterpriseNumber +
                '}';
    }

    public static class Builder {
        private boolean enterpriseBit;
        private int informationElementIdentifier;
        private int fieldLength;
        private long enterpriseNumber;

        private Builder() {
        }

        public Builder enterpriseBit(boolean enterpriseBit) {
            this.enterpriseBit = enterpriseBit;
            return this;
        }

        public Builder informationElementIdentifier(int informationElementIdentifier) {
            this.informationElementIdentifier = informationElementIdentifier;
            return this;
        }

        public Builder fieldLength(int fieldLength) {
            this.fieldLength = fieldLength;
            return this;
        }

        public Builder enterpriseNumber(long enterpriseNumber) {
            this.enterpriseNumber = enterpriseNumber;
            return this;
        }

        public IPFIXFieldSpecifier build() {
            return new IPFIXFieldSpecifier(this);
        }
    }
}