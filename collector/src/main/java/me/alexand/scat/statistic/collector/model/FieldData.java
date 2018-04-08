package me.alexand.scat.statistic.collector.model;

/**
 * Единица данных определенного типа
 *
 * @author asidorov84@gmail.com
 */

public class FieldData {
    private final String name;
    private final Object data;
    private final IANAAbstractDataTypes type;

    public FieldData(String name, Object data, IANAAbstractDataTypes type) {
        this.name = name;
        this.data = data;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Object getData() {
        return data;
    }

    public IANAAbstractDataTypes getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FieldData{" +
                "name='" + name + '\'' +
                ", data=" + data +
                ", type=" + type +
                '}';
    }
}