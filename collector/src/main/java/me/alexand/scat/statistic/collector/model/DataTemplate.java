package me.alexand.scat.statistic.collector.model;

import java.util.List;
import java.util.Objects;

/**
 * Шаблон с описанием структуры выгружаемых данных
 *
 * @author asidorov84@gmail.com
 */
public class DataTemplate {
    private final TemplateType type;
    private final List<IPFIXFieldSpecifier> fieldSpecifiers;

    private DataTemplate(DataTemplate.Builder builder) {
        this.type = builder.type;
        this.fieldSpecifiers = builder.fieldSpecifiers;
    }

    public static DataTemplate.Builder builder() {
        return new DataTemplate.Builder();
    }

    public TemplateType getType() {
        return type;
    }

    public List<IPFIXFieldSpecifier> getFieldSpecifiers() {
        return fieldSpecifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTemplate template = (DataTemplate) o;
        return type == template.type &&
                Objects.equals(fieldSpecifiers, template.fieldSpecifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, fieldSpecifiers);
    }

    @Override
    public String toString() {
        return "DataTemplate{" +
                "type=" + type +
                ", fieldSpecifiers=" + fieldSpecifiers +
                '}';
    }

    public static class Builder {
        private TemplateType type;
        private List<IPFIXFieldSpecifier> fieldSpecifiers;

        private Builder() {
        }


        public Builder type(TemplateType type) {
            this.type = type;
            return this;
        }

        public Builder fieldSpecifiers(List<IPFIXFieldSpecifier> fieldSpecifiers) {
            this.fieldSpecifiers = fieldSpecifiers;
            return this;
        }

        public DataTemplate build() {
            return new DataTemplate(this);
        }
    }
}