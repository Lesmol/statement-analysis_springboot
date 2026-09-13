package org.lvmp.statementanalysis_springboot.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record ApplicationConfigurationProperties(CognitoConfiguration cognito, S3Configuration s3, SnsConfiguration sns,
                                                 KmsConfiguration kms, RdsConfiguration rds) {

    public record CognitoConfiguration(String userPoolId, String clientId, String clientSecret) {
    }

    public record S3Configuration(String bucketName) {
    }

    public record SnsConfiguration(String topic, String role) {
    }

    public record KmsConfiguration(String keyId) {
    }

    public record RdsConfiguration(String dbName, String dbClusterArn, String dbSecretArn) {
    }

}
