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

package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.*;
import me.alexand.scat.statistic.collector.repository.ImportDataTemplateRepository;
import me.alexand.scat.statistic.collector.utils.exceptions.IPFIXParseException;
import me.alexand.scat.statistic.collector.utils.exceptions.MalformedMessageException;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownDataRecordFormatException;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.copyOfRange;
import static me.alexand.scat.statistic.collector.model.IPFIXHeader.IPFIX_MESSAGE_HEADER_LENGTH;
import static me.alexand.scat.statistic.collector.model.IPFIXHeader.IPFIX_MESSAGE_VERSION;
import static me.alexand.scat.statistic.collector.utils.BytesConvertUtils.*;

/**
 * Класс для декодирования IPFIX-сообщений из набора байт
 * <p>
 *
 * @author asidorov84@gmail.com
 */

@Component
public class IPFIXParser {
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(3);
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private static final Logger LOGGER = LoggerFactory.getLogger(IPFIXParser.class);

    private final Map<Long, IPFIXTemplateRecord> IPFIXTemplates = new ConcurrentHashMap<>();
    private final ImportDataTemplateRepository templateRepository;

    public IPFIXParser(ImportDataTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    /**
     * Метод для декодирования IPFIX-сообщения
     *
     * @param payload массив байт, содержащий сообщение (обязательный параметр)
     * @return сообщение в виде экземпляра класса IPFIXMessage
     * @throws NullPointerException если параметр {@code payload} равен {@code null}
     * @throws IPFIXParseException  при возникновении ошибки во время декодирования
     * @see IPFIXMessage
     */
    public IPFIXMessage parse(byte[] payload) throws IPFIXParseException {
        if (payload == null) {
            throw new NullPointerException("Payload must not be NULL");
        }

        int payloadLength = payload.length;

        if (payloadLength < IPFIX_MESSAGE_HEADER_LENGTH) {
            throw new MalformedMessageException(String.format("Length of message less than %d (minimal): %d bytes",
                    IPFIX_MESSAGE_HEADER_LENGTH,
                    payloadLength));
        }

        IPFIXHeader header = parseHeader(payload);
        int messageLength = header.getLength();

        if (messageLength != payloadLength) {
            throw new MalformedMessageException(String.format("Length of message (%d) not equal payload length (%d)",
                    messageLength,
                    payloadLength));
        }

        return IPFIXMessage.builder()
                .header(header)
                .dataRecords(parseSets(header.getObservationDomainID(), header.getExportTime(), payload))
                .build();
    }


    /**
     * Метод для декодирования заголовка IPFIX-сообщения.<br>
     * На входе ожидается массив байт не менее чем из {@code IPFIX_MESSAGE_HEADER_LENGTH} элементов
     *
     * @param payload массив байт, содержащий заголовок (обязательный параметр)
     * @return заголовок сообщения в виде экземпляра класса IPFIXHeader
     * @throws NullPointerException      если параметр {@code payload} равен {@code null}
     * @throws UnknownProtocolException  если версия протокола не равна {@code IPFIX_MESSAGE_VERSION}
     * @throws MalformedMessageException если длина {@code payload} меньше чем {@code IPFIX_MESSAGE_HEADER_LENGTH}
     * @see IPFIXHeader
     * @see IPFIXHeader#IPFIX_MESSAGE_VERSION
     * @see IPFIXHeader#IPFIX_MESSAGE_HEADER_LENGTH
     */
    private IPFIXHeader parseHeader(byte[] payload) throws UnknownProtocolException, MalformedMessageException {
        if (payload == null) {
            throw new NullPointerException("Payload must not be NULL");
        }

        try {
            int offset = 0;
            int version = twoBytesToInt(payload, offset);
            offset += 2;

            if (version != IPFIX_MESSAGE_VERSION) {
                throw new UnknownProtocolException(String.format("Version of message must be %d, but actual is %d",
                        IPFIXHeader.IPFIX_MESSAGE_VERSION,
                        version));
            }

            int length = twoBytesToInt(payload, offset);
            offset += 2;

            long exportTime = fourBytesToLong(payload, offset);
            offset += 4;

            long sequenceNumber = fourBytesToLong(payload, offset);
            offset += 4;

            long observationDomainID = fourBytesToLong(payload, offset);

            return IPFIXHeader.builder()
                    .length(length)
                    .exportTime(exportTime)
                    .sequenceNumber(sequenceNumber)
                    .observationDomainID(observationDomainID)
                    .build();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MalformedMessageException(e);
        }
    }

    /**
     * Метод для декодирования сетов IPFIX-сообщений.<br>
     *
     * @param observationDomainID домен экспортера
     * @param exportTime          время экспорта
     * @param payload             массив байт, содержащий сообщение
     * @return список IPFIXDataRecord
     * @throws IPFIXParseException в случае ошибки при декодировании
     */
    private List<IPFIXDataRecord> parseSets(long observationDomainID,
                                            long exportTime,
                                            byte[] payload) throws IPFIXParseException {
        if (payload == null) {
            throw new NullPointerException("Payload must not be NULL");
        }

        List<IPFIXDataRecord> records = new ArrayList<>();

        int offset = IPFIX_MESSAGE_HEADER_LENGTH;

        try {
            while (offset < payload.length) {
                int setID = twoBytesToInt(payload, offset);
                offset += 2;

                int length = twoBytesToInt(payload, offset);
                offset += 2;

                byte[] recordsPayload = copyOfRange(payload, offset, offset + length);

                if (setID == 2) {
                    parseTemplateRecords(observationDomainID, exportTime, recordsPayload);
                }

                if (setID >= 256 && setID <= 65535) {
                    records.addAll(parseDataRecords(observationDomainID, setID, exportTime, recordsPayload));
                }

                offset += (length - 4);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MalformedMessageException(e);
        }

        return records;
    }

    private void parseTemplateRecords(long observationDomainID,
                                      long exportTime,
                                      byte[] payload) throws IPFIXParseException {
        if (payload == null) {
            throw new NullPointerException("Payload must not be NULL");
        }

        int minTemplateRecordLength = 8;
        int offset = 0;

        try {
            while (payload.length - offset >= minTemplateRecordLength) {
                int currentRecordLength = 0;
                int templateID = twoBytesToInt(payload, offset);
                offset += 2;

                int fieldCount = twoBytesToInt(payload, offset);
                offset += 2;
                currentRecordLength += 4;

                List<InfoModelEntity> infoModelEntities = new ArrayList<>(fieldCount);

                for (int i = 0; i < fieldCount; i++) {
                    boolean enterpriseBit = isHighBitSet(payload[offset]);

                    payload[offset] &= 0x7f;
                    int informationElementIdentifier = twoBytesToInt(payload, offset);
                    offset += 2;
                    currentRecordLength += 2;

                    //int fieldLength = twoBytesToInt(payload, offset);
                    offset += 2;
                    currentRecordLength += 2;

                    long enterpriseNumber = enterpriseBit ? fourBytesToLong(payload, offset) : 0;
                    offset += enterpriseBit ? 4 : 0;
                    currentRecordLength += enterpriseBit ? 4 : 0;

                    InfoModelEntity infoModelEntity = templateRepository.getInfoModel(enterpriseNumber, informationElementIdentifier);

                    infoModelEntities.add(infoModelEntity);
                }

                ImportDataTemplate dataTemplate = templateRepository.findByInfoModelEntities(infoModelEntities);

                IPFIXTemplateRecord templateRecord = IPFIXTemplateRecord.builder()
                        .templateID(templateID)
                        .exportTime(exportTime)
                        .dataTemplate(dataTemplate)
                        .build();

                minTemplateRecordLength = Math.min(minTemplateRecordLength, currentRecordLength);
                registerTemplateRecord(observationDomainID, templateRecord);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MalformedMessageException(e);
        }
    }

    private List<IPFIXDataRecord> parseDataRecords(long observationDomainID,
                                                   int setID,
                                                   long exportTime,
                                                   byte[] payload) throws IPFIXParseException {
        Objects.requireNonNull(payload);
        List<IPFIXDataRecord> records = new ArrayList<>();

        IPFIXTemplateRecord templateRecord = getTemplateRecord(observationDomainID, setID);

        if (templateRecord == null || templateRecord.getExportTime() > exportTime) {
            throw new UnknownDataRecordFormatException(
                    String.format("Can't find template for ObservationDomainID: %d and Data Record ID: %d",
                            observationDomainID,
                            setID));
        }

        int offset = 0;
        int minDataRecordLength = templateRecord.getMinDataRecordSize();

        while (payload.length - offset >= minDataRecordLength) {
            int fieldCount = templateRecord.getFieldCount();
            List<IPFIXFieldValue> fieldValues = new ArrayList<>(fieldCount);
            int currentRecordLength = 0;

            for (InfoModelEntity infoModelEntity : templateRecord.getDataTemplate().getSpecifiers()) {
                IANAAbstractDataTypes fieldType = infoModelEntity.getType();
                int fieldLength = fieldType.getLength();

                Object value = null;

                switch (fieldType) {
                    case DATE_TIME_SECONDS:
                        long epochSeconds = fourBytesToLong(payload, offset);
                        value = LocalDateTime.ofEpochSecond(epochSeconds, 0, ZONE_OFFSET);
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case DATE_TIME_MILLISECONDS:
                        BigInteger epochMilliseconds = eightBytesToBigInteger(payload, offset);
                        value = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilliseconds.longValue()), ZONE_ID);
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case UNSIGNED8:
                        value = oneByteToInt(payload[offset]);
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case UNSIGNED16:
                        value = twoBytesToInt(payload, offset);
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case UNSIGNED32:
                        value = fourBytesToLong(payload, offset);
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case UNSIGNED64:
                        value = eightBytesToBigInteger(payload, offset);
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case IPV4_ADDRESS:
                        value = fourBytesToIPv4(payload, offset);
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case STRING:
                        int actualFieldLength = oneByteToInt(payload[offset]);
                        offset += 1;
                        currentRecordLength += 1;

                        if (actualFieldLength == 255) {
                            actualFieldLength = twoBytesToInt(payload, offset);
                            offset += 2;
                            currentRecordLength += 2;
                        }

                        value = bytesToString(payload, offset, actualFieldLength);
                        offset += actualFieldLength;
                        currentRecordLength += actualFieldLength;
                        break;
                }

                fieldValues.add(IPFIXFieldValue.builder()
                        .name(infoModelEntity.getName())
                        .type(fieldType)
                        .value(value)
                        .build());
            }

            records.add(IPFIXDataRecord.builder()
                    .dataTemplate(templateRecord.getDataTemplate())
                    .fieldValues(fieldValues)
                    .build());

            minDataRecordLength = Math.min(minDataRecordLength, currentRecordLength);
        }

        return records;
    }

    private void registerTemplateRecord(long observationDomainID,
                                        IPFIXTemplateRecord templateRecord) {
        Long registrationID = getRegistrationID(observationDomainID, templateRecord.getTemplateID());

        IPFIXTemplates.merge(registrationID, templateRecord,
                (oldRecord, newRecord) -> oldRecord.getExportTime() < newRecord.getExportTime() ? newRecord : oldRecord);

        LOGGER.info("register received IPFIX template: {}", templateRecord);
    }

    private IPFIXTemplateRecord getTemplateRecord(long observationDomainID, int templateID) {
        return IPFIXTemplates.get(getRegistrationID(observationDomainID, templateID));
    }

    private long getRegistrationID(long observationDomainID, int templateID) {
        return observationDomainID << 16 | templateID;
    }
}