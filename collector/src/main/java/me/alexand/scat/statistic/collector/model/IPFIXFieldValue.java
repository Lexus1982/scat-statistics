package me.alexand.scat.statistic.collector.model;

import java.util.Objects;

/**
 * Единица данных определенного типа
 *
 * @author asidorov84@gmail.com
 */

public class IPFIXFieldValue {
    private final String name;
    private final Object value;
    private final IANAAbstractDataTypes type;

    public static IPFIXFieldValue.Builder builder() {
        return new IPFIXFieldValue.Builder();
    }

    private IPFIXFieldValue(IPFIXFieldValue.Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.type = builder.type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public IANAAbstractDataTypes getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXFieldValue that = (IPFIXFieldValue) o;
        return Objects.equals(name, that.name) &&
                //Objects.equals(value, that.value) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, type);
    }

    @Override
    public String toString() {
        return "IPFIXFieldValue{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type=" + type +
                '}';
    }

    public static class Builder {
        private String name;
        private Object value;
        private IANAAbstractDataTypes type;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder type(IANAAbstractDataTypes type) {
            this.type = type;
            return this;
        }

        public IPFIXFieldValue build() {
            return new IPFIXFieldValue(this);
        }
    }
}