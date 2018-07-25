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
 * Класс с описанием шаблона выгружаемых из платформы СКАТ данных
 *
 * @author asidorov84@gmail.com
 */
public final class SCATDataTemplate {
    private final TemplateType type;
    private final List<InfoModelEntity> specifiers;

    private SCATDataTemplate(SCATDataTemplate.Builder builder) {
        this.type = builder.type;
        this.specifiers = builder.specifiers;
    }

    public static SCATDataTemplate.Builder builder() {
        return new SCATDataTemplate.Builder();
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
        SCATDataTemplate template = (SCATDataTemplate) o;
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

        public SCATDataTemplate build() {
            return new SCATDataTemplate(this);
        }
    }
}