package me.alexand.scat.statistic.collector.utils;

import me.alexand.scat.statistic.collector.model.DataTemplate;

import java.util.List;

import static java.util.Arrays.asList;
import static me.alexand.scat.statistic.collector.model.TemplateType.*;
import static me.alexand.scat.statistic.collector.utils.InfoModelEntities.*;

/**
 * @author asidorov84@gmail.com
 */
public interface DataTemplateEntities {

    DataTemplate CS_REQ_TEMPLATE = DataTemplate.builder()
            .type(CS_REQ)
            .specifiers(asList(TIMESTAMP,
                    LOGIN,
                    SOURCE_IP,
                    DESTINATION_IP,
                    HOSTNAME,
                    PATH,
                    REFER,
                    USER_AGENT,
                    COOKIE,
                    SESSION_ID,
                    LOCKED,
                    HOST_TYPE,
                    METHOD))
            .build();

    DataTemplate CS_RESP_TEMPLATE = DataTemplate.builder()
            .type(CS_RESP)
            .specifiers(asList(TIMESTAMP,
                    LOGIN,
                    SOURCE_IP,
                    DESTINATION_IP,
                    RESULT_CODE,
                    CONTENT_LENGTH,
                    CONTENT_TYPE,
                    SESSION_ID))
            .build();

    DataTemplate GENERIC_TEMPLATE = DataTemplate.builder()
            .type(GENERIC)
            .specifiers(asList(OCTET_DELTA_COUNT,
                    PACKET_DELTA_COUNT,
                    PROTOCOL_IDENTIFIER,
                    IP_CLASS_OF_SERVICE,
                    SOURCE_TRANSPORT_PORT,
                    SOURCE_IPV4_ADDRESS,
                    DESTINATION_TRANSPORT_PORT,
                    DESTINATION_IPV4_ADDRESS,
                    BGP_SOURCE_AS,
                    BGP_DESTINATION_AS,
                    FLOW_START,
                    FLOW_END,
                    IN_SNMP,
                    OUT_SNMP,
                    IP_VERSION,
                    SESSION_ID,
                    HTTP_HOST,
                    DPI_PROTOCOL,
                    LOGIN_2,
                    PORT_NAT_SRC_ADDR,
                    PORT_NAT_SRC_PORT))
            .build();

    List<DataTemplate> DATA_TEMPLATE_LIST = asList(CS_REQ_TEMPLATE, CS_RESP_TEMPLATE, GENERIC_TEMPLATE);
}