package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.model.*;
import me.alexand.scat.statistic.collector.repository.InfoModelRepository;
import me.alexand.scat.statistic.collector.service.DataTemplateService;
import me.alexand.scat.statistic.collector.service.IPFIXParser;
import me.alexand.scat.statistic.collector.service.StatCollector;
import me.alexand.scat.statistic.collector.utils.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static me.alexand.scat.statistic.collector.utils.BytesConvertUtils.*;

/**
 * @author asidorov84@gmail.com
 */

@Service
public class IPFIXParserImpl implements IPFIXParser {
    private static final int MESSAGE_HEADER_LENGTH = 16;
    private static final int IPFIX_MESSAGE_VERSION = 0x0a;

    private final DataTemplateService dataTemplateService;
    private final InfoModelRepository infoModelRepository;
    //TODO выпилить регистратор статистики, ее собирать в процессоре
    private final StatCollector statCollector;

    private final Map<Long, IPFIXTemplateRecord> templates = new ConcurrentHashMap<>();

    @Autowired
    public IPFIXParserImpl(DataTemplateService dataTemplateService,
                           InfoModelRepository infoModelRepository,
                           StatCollector statCollector) {
        this.dataTemplateService = dataTemplateService;
        this.infoModelRepository = infoModelRepository;
        this.statCollector = statCollector;
    }

    @Override
    public IPFIXMessage parse(byte[] payload) throws IPFIXParseException {
        Objects.requireNonNull(payload);

        if (payload.length < MESSAGE_HEADER_LENGTH) {
            statCollector.registerMalformedPacket();
            throw new MalformedMessageException(String.format("invalid IPFIX message length: %d bytes", payload.length));
        }

        byte[] headerPDU = copyOf(payload, MESSAGE_HEADER_LENGTH);
        byte[] setsPDU = copyOfRange(payload, MESSAGE_HEADER_LENGTH, payload.length);

        IPFIXHeader header = parseHeader(headerPDU);

        if (header.getLength() != payload.length) {
            statCollector.registerMalformedPacket();
            throw new MalformedMessageException(String.format("invalid IPFIX message length: %d bytes", payload.length));
        }

        return IPFIXMessage.builder()
                .header(header)
                .sets(parseSets(header.getObservationDomainID(), header.getExportTime(), setsPDU))
                .build();
    }

    private IPFIXHeader parseHeader(byte[] payload) throws IPFIXParseException {
        Objects.requireNonNull(payload);

        try {
            int offset = 0;
            int version = twoBytesToInt(copyOfRange(payload, offset, offset + 2));
            offset += 2;

            if (version != IPFIX_MESSAGE_VERSION) {
                statCollector.registerUnknownProtocolPacket();
                throw new UnknownProtocolException(String.format("version must be 10, but actual is %d", version));
            }

            int length = twoBytesToInt(copyOfRange(payload, offset, offset + 2));
            offset += 2;

            long exportTime = fourBytesToLong(copyOfRange(payload, offset, offset + 4));
            offset += 4;

            long sequenceNumber = fourBytesToLong(copyOfRange(payload, offset, offset + 4));
            offset += 4;

            long observationDomainID = fourBytesToLong(copyOfRange(payload, offset, offset + 4));

            return IPFIXHeader.builder()
                    .version(version)
                    .length(length)
                    .exportTime(exportTime)
                    .sequenceNumber(sequenceNumber)
                    .observationDomainID(observationDomainID)
                    .build();

        } catch (ArrayIndexOutOfBoundsException e) {
            statCollector.registerMalformedPacket();
            throw new MalformedMessageException(e);
        }
    }

    private List<IPFIXSet> parseSets(long observationDomainId,
                                     long exportTime,
                                     byte[] payload) throws IPFIXParseException {
        Objects.requireNonNull(payload);
        List<IPFIXSet> sets = new ArrayList<>();

        int offset = 0;

        try {
            while (offset < payload.length) {
                int setID = twoBytesToInt(copyOfRange(payload, offset, offset + 2));
                offset += 2;

                int length = twoBytesToInt(copyOfRange(payload, offset, offset + 2));
                offset += 2;

                List<AbstractIPFIXRecord> records = parseRecords(observationDomainId,
                        setID,
                        exportTime,
                        copyOfRange(payload, offset, offset + length));

                offset += length - 4;

                sets.add(IPFIXSet.builder()
                        .setID(setID)
                        .length(length)
                        .records(records)
                        .build());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            statCollector.registerMalformedPacket();
            throw new MalformedMessageException(e);
        }

        return sets;
    }

    private List<AbstractIPFIXRecord> parseRecords(long observationDomainID,
                                                   int setID,
                                                   long exportTime,
                                                   byte[] payload) throws IPFIXParseException {
        Objects.requireNonNull(payload);
        List<AbstractIPFIXRecord> records = new ArrayList<>();

        if (setID == 2) {
            records.addAll(parseTemplateRecords(observationDomainID, exportTime, payload));
        }

        if (setID >= 256 && setID <= 65535) {
            records.addAll(parseDataRecords(observationDomainID,
                    setID,
                    exportTime,
                    payload));
        }

        return records;
    }

    private List<IPFIXTemplateRecord> parseTemplateRecords(long observationDomainID,
                                                           long exportTime,
                                                           byte[] payload) throws IPFIXParseException {
        Objects.requireNonNull(payload);
        List<IPFIXTemplateRecord> records = new ArrayList<>();

        int minTemplateRecordLength = 8;
        int offset = 0;

        try {
            while (payload.length - offset >= minTemplateRecordLength) {
                int currentRecordLength = 0;
                int templateID = twoBytesToInt(copyOfRange(payload, offset, offset + 2));
                offset += 2;

                int fieldCount = twoBytesToInt(copyOfRange(payload, offset, offset + 2));
                offset += 2;
                currentRecordLength += 4;

                List<IPFIXFieldSpecifier> fieldSpecifiers = new ArrayList<>(fieldCount);

                for (int i = 0; i < fieldCount; i++) {
                    boolean enterpriseBit = isHighBitSet(payload[offset]);

                    payload[offset] &= 0x7f;
                    int informationElementIdentifier = twoBytesToInt(copyOfRange(payload, offset, offset + 2));
                    offset += 2;
                    currentRecordLength += 2;

                    int fieldLength = twoBytesToInt(copyOfRange(payload, offset, offset + 2));
                    offset += 2;
                    currentRecordLength += 2;

                    long enterpriseNumber = enterpriseBit ? fourBytesToLong(copyOfRange(payload, offset, offset + 4)) : 0;
                    offset += enterpriseBit ? 4 : 0;
                    currentRecordLength += enterpriseBit ? 4 : 0;

                    IPFIXFieldSpecifier fs = IPFIXFieldSpecifier.builder()
                            .enterpriseBit(enterpriseBit)
                            .informationElementIdentifier(informationElementIdentifier)
                            .fieldLength(fieldLength)
                            .enterpriseNumber(enterpriseNumber)
                            .build();

                    fieldSpecifiers.add(fs);
                }

                TemplateType type = dataTemplateService.getTypeByIPFIXSpecifiers(fieldSpecifiers);

                IPFIXTemplateRecord record = IPFIXTemplateRecord.builder()
                        .templateID(templateID)
                        .fieldCount(fieldCount)
                        .exportTime(exportTime)
                        .type(type)
                        .fieldSpecifiers(fieldSpecifiers)
                        .build();

                minTemplateRecordLength = Math.min(minTemplateRecordLength, currentRecordLength);
                registerTemplateRecord(observationDomainID, record);
                records.add(record);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            statCollector.registerMalformedPacket();
            throw new MalformedMessageException(e);
        } catch (UnknownInfoModelException e) {
            statCollector.registerUnknownInfoModel();
            throw e;
        }

        return records;
    }

    private List<IPFIXDataRecord> parseDataRecords(long observationDomainID,
                                                   int setID,
                                                   long exportTime,
                                                   byte[] payload) throws IPFIXParseException {
        Objects.requireNonNull(payload);
        List<IPFIXDataRecord> records = new ArrayList<>();

        IPFIXTemplateRecord templateRecord = getTemplateRecord(observationDomainID, setID);

        if (templateRecord == null || templateRecord.getExportTime() > exportTime) {
            statCollector.registerUnknownDataFormatPacket();
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

            for (IPFIXFieldSpecifier specifier : templateRecord.getFieldSpecifiers()) {
                long enterpriseNumber = specifier.getEnterpriseNumber();
                int informationElementIdentifier = specifier.getInformationElementIdentifier();
                int fieldLength = specifier.getFieldLength();

                InfoModelEntity entity = infoModelRepository.getByEnterpriseNumberAndInformationElementIdentifier(
                        enterpriseNumber,
                        informationElementIdentifier
                );

                Object value = null;

                switch (entity.getType()) {
                    case DATE_TIME_SECONDS:
                        long epochSeconds = fourBytesToLong(copyOfRange(payload, offset, offset + fieldLength));
                        value = LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.ofHours(3));
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case DATE_TIME_MILLISECONDS:
                        BigInteger epochMilliseconds = eightBytesToBigInteger(copyOfRange(payload, offset, offset + fieldLength));
                        value = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilliseconds.longValue()), ZoneId.systemDefault());
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case UNSIGNED8:
                        value = oneByteToInt(payload[offset]);
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case UNSIGNED16:
                        value = twoBytesToInt(copyOfRange(payload, offset, offset + fieldLength));
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case UNSIGNED32:
                        value = fourBytesToLong(copyOfRange(payload, offset, offset + fieldLength));
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case UNSIGNED64:
                        value = eightBytesToBigInteger(copyOfRange(payload, offset, offset + fieldLength));
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case IPV4_ADDRESS:
                        try {
                            value = fourBytesToIPv4(copyOfRange(payload, offset, offset + fieldLength)).getHostAddress();
                        } catch (UnknownHostException e) {
                            throw new IPFIXParseException(e);
                        }
                        offset += fieldLength;
                        currentRecordLength += fieldLength;
                        break;
                    case STRING:
                        int actualFieldLength = oneByteToInt(payload[offset]);
                        offset += 1;
                        currentRecordLength += 1;

                        if (actualFieldLength == 255) {
                            actualFieldLength = twoBytesToInt(copyOfRange(payload, offset, offset + fieldLength));
                            offset += 2;
                            currentRecordLength += 2;
                        }

                        value = bytesToString(copyOfRange(payload, offset, offset + actualFieldLength));
                        offset += actualFieldLength;
                        currentRecordLength += actualFieldLength;
                        break;
                }

                fieldValues.add(IPFIXFieldValue.builder()
                        .name(entity.getName())
                        .type(entity.getType())
                        .value(value)
                        .build());
            }

            records.add(IPFIXDataRecord.builder()
                    .type(templateRecord.getType())
                    .fieldValues(fieldValues)
                    .build());

            statCollector.registerDataRecord();

            minDataRecordLength = Math.min(minDataRecordLength, currentRecordLength);
        }

        return records;
    }

    private void registerTemplateRecord(long observationDomainID,
                                        IPFIXTemplateRecord record) {
        Long registrationID = getRegistrationID(observationDomainID, record.getTemplateID());

        templates.merge(registrationID, record, (oldRecord, newRecord) -> {
            if (oldRecord.getExportTime() < newRecord.getExportTime()) {
                return newRecord;
            }

            return oldRecord;
        });
        statCollector.registerTemplateRecord();
    }

    private IPFIXTemplateRecord getTemplateRecord(long observationDomainID, int templateID) {
        return templates.get(getRegistrationID(observationDomainID, templateID));
    }

    private long getRegistrationID(long observationDomainID, int templateID) {
        return observationDomainID << 16 | templateID;
    }
}