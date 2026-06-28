package org.lvmp.statementanalysis_springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;

@Configuration
public class TextractConfig {

    @Bean
    public TextractClient textractClient() {
        return TextractClient.builder()
                .region(Region.EU_WEST_1)
                .build();
    }
}
