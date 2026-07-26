package org.lvmp.statementanalysis_springboot.repository;

import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;
import software.amazon.awssdk.services.rdsdata.model.TypeHint;

import java.time.Instant;
import java.util.UUID;

final class SqlParameters {

    static SqlParameter stringParam(String name, String value) {
        return SqlParameter.builder().name(name).value(Field.builder().stringValue(value).build()).build();
    }

    static SqlParameter uuidParam(String name, UUID value) {
        return SqlParameter.builder().name(name)
                .value(Field.builder().stringValue(value.toString()).build())
                .typeHint(TypeHint.UUID)
                .build();
    }

    static SqlParameter timestampParam(String name, Instant value) {
        return SqlParameter.builder().name(name)
                .value(Field.builder().stringValue(value.toString()).build())
                .typeHint(TypeHint.TIMESTAMP)
                .build();
    }
}
