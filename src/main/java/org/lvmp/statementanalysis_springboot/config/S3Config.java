package org.lvmp.statementanalysis_springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String region;

    /**
     * Create and configure an AWS S3 client using the application's configured AWS region.
     *
     * @return an S3Client configured for the AWS region specified by the `aws.region` application property
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
}
