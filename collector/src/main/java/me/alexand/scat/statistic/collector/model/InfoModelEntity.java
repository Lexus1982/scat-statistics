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

import java.util.Objects;

/**
 * Класс, описывающий информационный элемент шаблона АПК "СКАТ".
 *
 * @author asidorov84@gmail.com
 * @see IANAAbstractDataTypes
 */
public final class InfoModelEntity {
    //Уникальный идентификатор организации, согласно IANA
    private final long enterpriseNumber;

    //Уникальный идентификатор информационного элемента в рамках данной организации
    private final int informationElementId;

    //Тип данных информационного элемента, согласно IANA
    private final IANAAbstractDataTypes type;

    //Имя информационного элемента
    private final String name;

    public static InfoModelEntity.Builder builder() {
        return new InfoModelEntity.Builder();
    }

    private InfoModelEntity(InfoModelEntity.Builder builder) {
        this.informationElementId = builder.informationElementId;
        this.type = builder.type;
        this.enterpriseNumber = builder.enterpriseNumber;
        this.name = builder.name;
    }

    public long getEnterpriseNumber() {
        return enterpriseNumber;
    }

    public int getInformationElementId() {
        return informationElementId;
    }

    public IANAAbstractDataTypes getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoModelEntity that = (InfoModelEntity) o;
        return enterpriseNumber == that.enterpriseNumber &&
                informationElementId == that.informationElementId &&
                type == that.type &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enterpriseNumber, informationElementId, type, name);
    }

    @Override
    public String toString() {
        return "InfoModelEntity{" +
                "enterpriseNumber=" + enterpriseNumber +
                ", informationElementId=" + informationElementId +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }

    public static class Builder {
        private long enterpriseNumber;
        private int informationElementId;
        private IANAAbstractDataTypes type;
        private String name;

        private Builder() {
        }

        public Builder enterpriseNumber(long enterpriseNumber) {
            this.enterpriseNumber = enterpriseNumber;
            return this;
        }

        public Builder informationElementId(int informationElementId) {
            this.informationElementId = informationElementId;
            return this;
        }

        public Builder type(IANAAbstractDataTypes type) {
            this.type = type;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public InfoModelEntity build() {
            return new InfoModelEntity(this);
        }
    }
}