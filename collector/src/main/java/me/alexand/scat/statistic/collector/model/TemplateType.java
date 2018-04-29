package me.alexand.scat.statistic.collector.model;

/**
 * @author asidorov84@gmail.com
 */
public enum TemplateType {
    GENERIC("generic"),
    CS_REQ("cs_req"),
    CS_RESP("cs_resp"),
    UNKNOWN("null");

    private String name;

    TemplateType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}