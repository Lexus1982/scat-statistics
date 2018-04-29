package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.IPFIXMessage;
import me.alexand.scat.statistic.collector.repository.DataTemplateRepository;
import me.alexand.scat.statistic.collector.repository.InfoModelRepository;
import me.alexand.scat.statistic.collector.utils.exceptions.MalformedMessageException;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.alexand.scat.statistic.collector.util.IPFIXMessageTestEntities.IPFIX_MESSAGE_WITH_CS_REQ_DATA;
import static me.alexand.scat.statistic.collector.util.IPFIXMessageTestEntities.IPFIX_MESSAGE_WITH_CS_REQ_TEMPLATE;
import static me.alexand.scat.statistic.collector.util.RawPacketsEntities.*;
import static me.alexand.scat.statistic.collector.utils.DataTemplateEntities.DATA_TEMPLATE_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Тесты IPFIX-парсера
 *
 * @author asidorov84@gmail.com
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-app.xml")
public class IPFIXParserTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPFIXParserTests.class);

    @Autowired
    private IPFIXParser parser;

    @Autowired
    private DataTemplateRepository dataTemplateRepository;

    @Autowired
    private InfoModelRepository infoModelRepository;

    @Before
    public void before() {
        DATA_TEMPLATE_LIST.forEach(dataTemplate -> {
            dataTemplateRepository.save(dataTemplate);
            dataTemplate.getSpecifiers().forEach(infoModelRepository::save);
        });
    }

    @Test
    public void testParseCSREQTemplate() throws Exception {
        IPFIXMessage actual = parser.parse(RAW_GENERIC_TEMPLATE);
        assertNotNull(actual);

        LOGGER.info("\n\nexpected:\n\t {}\n\nactual:\n\t {}\n", IPFIX_MESSAGE_WITH_CS_REQ_TEMPLATE, actual);

        assertEquals(IPFIX_MESSAGE_WITH_CS_REQ_TEMPLATE, actual);
    }

    @Test
    public void testParseCSREQData() throws Exception {
        assertNotNull(parser.parse(RAW_GENERIC_TEMPLATE));

        IPFIXMessage actual = parser.parse(RAW_CS_REQ_DATA_PAYLOAD);
        assertNotNull(actual);

        LOGGER.info("\n\nexpected:\n\t {}\n\nactual:\n\t {}\n", IPFIX_MESSAGE_WITH_CS_REQ_DATA, actual);

        assertEquals(IPFIX_MESSAGE_WITH_CS_REQ_DATA, actual);
    }

    @Test
    public void testParser() throws Exception {
        parser.parse(RAW_TEMPLATES_PAYLOAD);
    }

    @Test(expected = NullPointerException.class)
    public void testParseWithNullPayload() throws Exception {
        parser.parse(null);
    }

    @Test(expected = MalformedMessageException.class)
    public void testParseInvalidLengthPayload() throws Exception {
        parser.parse(INVALID_PAYLOAD);
    }


    @Test(expected = MalformedMessageException.class)
    public void testParseWithShortPayload() throws Exception {
        parser.parse(new byte[15]);
    }

    @Test(expected = UnknownProtocolException.class)
    public void testParseUnknownProtocolPayload() throws Exception {
        parser.parse(new byte[16]);
    }
}