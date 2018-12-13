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

public final class IPFIXTemplateRecord {
    private final ImportDataTemplate dataTemplate;
    private final int templateID;
    private final int fieldCount;
    private final long exportTime;

    public static IPFIXTemplateRecord.Builder builder() {
        return new IPFIXTemplateRecord.Builder();
    }

    private IPFIXTemplateRecord(IPFIXTemplateRecord.Builder builder) {
        this.dataTemplate = builder.dataTemplate;
        this.templateID = builder.templateID;
        this.fieldCount = builder.dataTemplate.getSpecifiers().size();
        this.exportTime = builder.exportTime;
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

    public ImportDataTemplate getDataTemplate() {
        return dataTemplate;
    }

    public int getMinDataRecordSize() {
        return dataTemplate.getSpecifiers().stream()
                .filter(s -> s.getType().getLength() != 65535)
                .map(s -> s.getType().getLength())
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
                Objects.equals(dataTemplate, that.dataTemplate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateID, fieldCount, exportTime, dataTemplate);
    }

    @Override
    public String toString() {
        return "IPFIXTemplateRecord{" +
                "templateID=" + templateID +
                ", fieldCount=" + fieldCount +
                ", exportTime=" + exportTime +
                ", dataTemplate=" + dataTemplate +
                '}';
    }

    public static class Builder {
        private int templateID;
        private long exportTime;
        private ImportDataTemplate dataTemplate;

        private Builder() {
        }

        public Builder templateID(int templateID) {
            this.templateID = templateID;
            return this;
        }

        public Builder exportTime(long exportTime) {
            this.exportTime = exportTime;
            return this;
        }

        public Builder dataTemplate(ImportDataTemplate dataTemplate) {
            this.dataTemplate = dataTemplate;
            return this;
        }

        public IPFIXTemplateRecord build() {
            return new IPFIXTemplateRecord(this);
        }
    }
}
