package org.lvmp.statementanalysis_springboot.shared.persistence;

import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;
import software.amazon.awssdk.services.rdsdata.model.TypeHint;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class SqlParameters {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

    private SqlParameters() {
    }

    public static SqlParameter stringParam(String name, String value) {
        return SqlParameter.builder().name(name).value(Field.builder().stringValue(value).build()).build();
    }

    public static SqlParameter uuidParam(String name, UUID value) {
        return SqlParameter.builder().name(name)
                .value(Field.builder().stringValue(value.toString()).build())
                .typeHint(TypeHint.UUID)
                .build();
    }

    public static SqlParameter timestampParam(String name, Instant value) {
        return SqlParameter.builder().name(name)
                .value(Field.builder().stringValue(TIMESTAMP_FORMATTER.format(value)).build())
                .typeHint(TypeHint.TIMESTAMP)
                .build();
    }

    public static Instant parseTimestamp(String value) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS]")
                .withZone(ZoneOffset.UTC)
                .parse(value, Instant::from);
    }
}
