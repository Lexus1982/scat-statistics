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
    private final List<InfoModelEntity> specifiers;

    private DataTemplate(DataTemplate.Builder builder) {
        this.type = builder.type;
        this.specifiers = builder.specifiers;
    }

    public static DataTemplate.Builder builder() {
        return new DataTemplate.Builder();
    }

    public TemplateType getType() {
        return type;
    }

    public List<InfoModelEntity> getSpecifiers() {
        return specifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTemplate template = (DataTemplate) o;
        return type == template.type &&
                Objects.equals(specifiers, template.specifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, specifiers);
    }

    @Override
    public String toString() {
        return "DataTemplate{" +
                "type=" + type +
                ", specifiers=" + specifiers +
                '}';
    }

    public static class Builder {
        private TemplateType type;
        private List<InfoModelEntity> specifiers;

        private Builder() {
        }

        public Builder type(TemplateType type) {
            this.type = type;
            return this;
        }

        public Builder specifiers(List<InfoModelEntity> specifiers) {
            this.specifiers = specifiers;
            return this;
        }

        public DataTemplate build() {
            return new DataTemplate(this);
        }
    }
}