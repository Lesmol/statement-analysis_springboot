package org.lvmp.statementanalysis_springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;

@Configuration
public class TextractConfig {

    @Value("${aws.region}")
    private String region;

    /**
     * Create a TextractClient configured with the application's AWS region.
     *
     * @return a TextractClient configured to use the region specified by the `aws.region` property
     */
    @Bean
    public TextractClient textractClient() {
        return TextractClient.builder()
                .region(Region.of(region))
                .build();
    }
}
