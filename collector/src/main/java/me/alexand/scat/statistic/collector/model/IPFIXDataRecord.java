package me.alexand.scat.statistic.collector.model;

import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
public class IPFIXDataRecord {
    private final long id;
    private final long observationDomainID;
    private final long exportTime;
    private final int templateID;
    private final List<FieldData> fieldData;

    public IPFIXDataRecord(long id,
                           long observationDomainID,
                           long exportTime,
                           int templateID,
                           List<FieldData> fieldData) {
        this.id = id;
        this.observationDomainID = observationDomainID;
        this.exportTime = exportTime;
        this.templateID = templateID;
        this.fieldData = fieldData;
    }

    public long getId() {
        return id;
    }

    public long getObservationDomainID() {
        return observationDomainID;
    }

    public long getExportTime() {
        return exportTime;
    }

    public int getTemplateID() {
        return templateID;
    }

    public List<FieldData> getFieldData() {
        return fieldData;
    }

    @Override
    public String toString() {
        return "IPFIXDataRecord{" +
                "observationDomainID=" + observationDomainID +
                ", exportTime=" + exportTime +
                ", templateID=" + templateID +
                ", fieldData=" + fieldData +
                '}';
    }

    public int getFieldsCount() {
        return fieldData.size();
    }
}