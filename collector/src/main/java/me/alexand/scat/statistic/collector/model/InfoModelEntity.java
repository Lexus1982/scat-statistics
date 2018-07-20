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
 * @author asidorov84@gmail.com
 */
public class InfoModelEntity {
    private int id;
    private int informationElementId;
    private IANAAbstractDataTypes type;
    private long enterpriseNumber;
    private String name;

    public static InfoModelEntity.Builder builder() {
        return new InfoModelEntity.Builder();
    }

    private InfoModelEntity(InfoModelEntity.Builder builder) {
        this.id = builder.id;
        this.informationElementId = builder.informationElementId;
        this.type = builder.type;
        this.enterpriseNumber = builder.enterpriseNumber;
        this.name = builder.name;
    }

    public int getId() {
        return id;
    }

    public int getInformationElementId() {
        return informationElementId;
    }

    public IANAAbstractDataTypes getType() {
        return type;
    }

    public long getEnterpriseNumber() {
        return enterpriseNumber;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInformationElementId(int informationElementId) {
        this.informationElementId = informationElementId;
    }

    public void setType(IANAAbstractDataTypes type) {
        this.type = type;
    }

    public void setEnterpriseNumber(long enterpriseNumber) {
        this.enterpriseNumber = enterpriseNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoModelEntity that = (InfoModelEntity) o;
        return id == that.id &&
                informationElementId == that.informationElementId &&
                enterpriseNumber == that.enterpriseNumber &&
                type == that.type &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, informationElementId, type, enterpriseNumber, name);
    }

    @Override
    public String toString() {
        return "InfoModelEntity{" +
                "id=" + id +
                ", informationElementId=" + informationElementId +
                ", type=" + type +
                ", enterpriseNumber=" + enterpriseNumber +
                ", name='" + name + '\'' +
                '}';
    }

    public static class Builder {
        private int id;
        private int informationElementId;
        private IANAAbstractDataTypes type;
        private long enterpriseNumber;
        private String name;

        private Builder() {
        }

        public Builder id(int id) {
            this.id = id;
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

        public Builder enterpriseNumber(long enterpriseNumber) {
            this.enterpriseNumber = enterpriseNumber;
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