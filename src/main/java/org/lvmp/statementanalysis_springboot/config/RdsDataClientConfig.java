package org.lvmp.statementanalysis_springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;

@Configuration
public class RdsDataClientConfig {

    @Bean
    public RdsDataClient rdsDataClient() {
        return RdsDataClient.builder()
                .region(Region.AF_SOUTH_1)
                .build();
    }
}
