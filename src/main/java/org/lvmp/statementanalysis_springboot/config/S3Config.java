package org.lvmp.statementanalysis_springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.encryption.s3.S3EncryptionClient;

@Configuration
public class S3Config {
    @Value("${aws.kms.key-id}")
    private String kmsKeyId;

    @Bean
    @Primary
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.EU_WEST_1)
                .build();
    }

    @Bean
    public S3EncryptionClient s3EncryptionClient() {
        return S3EncryptionClient.builderV4()
                .kmsKeyId(kmsKeyId)
                .enableLegacyUnauthenticatedModes(false)
                .build();
    }
}
