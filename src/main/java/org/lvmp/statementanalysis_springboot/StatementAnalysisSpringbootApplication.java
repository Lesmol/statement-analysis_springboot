package org.lvmp.statementanalysis_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StatementAnalysisSpringbootApplication {

    static void main(String[] args) {
        SpringApplication.run(StatementAnalysisSpringbootApplication.class, args);
    }

}
