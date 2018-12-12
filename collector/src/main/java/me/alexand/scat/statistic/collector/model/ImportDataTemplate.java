/*
 * Copyright 2018 Alexander Sidorov (asidorov84@gmail.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package me.alexand.scat.statistic.collector.model;

import java.util.List;
import java.util.Objects;

/**
 * Класс, описывающий шаблон выгружаемых данных
 *
 * @author asidorov84@gmail.com
 * @see TemplateType
 * @see InfoModelEntity
 */
public final class ImportDataTemplate {
    private final TemplateType type;
    private final List<InfoModelEntity> specifiers;

    private ImportDataTemplate(ImportDataTemplate.Builder builder) {
        this.type = builder.type;
        this.specifiers = builder.specifiers;
    }

    public static ImportDataTemplate.Builder builder() {
        return new ImportDataTemplate.Builder();
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
        ImportDataTemplate template = (ImportDataTemplate) o;
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

        public ImportDataTemplate build() {
            return new ImportDataTemplate(this);
        }
    }
}