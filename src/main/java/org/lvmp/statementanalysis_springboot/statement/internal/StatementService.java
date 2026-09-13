package org.lvmp.statementanalysis_springboot.statement.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.shared.config.properties.ApplicationConfigurationProperties;
import org.lvmp.statementanalysis_springboot.shared.context.UserContext;
import org.lvmp.statementanalysis_springboot.statement.UploadDocumentRequest;
import org.lvmp.statementanalysis_springboot.statement.UploadDocumentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatementService {
    private final S3Client s3Client;
    private final UserContext userContext;
    private final TextractClient textractClient;
    private final ApplicationConfigurationProperties configurationProperties;

    public ResponseEntity<UploadDocumentResponse> uploadDocument(UploadDocumentRequest request) throws IOException {
        String filename = userContext.getSub() + "/" + UUID.randomUUID();
        String bucketName = configurationProperties.s3().bucketName();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .contentType(request.getFile().getContentType())
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(
                        request.getFile().getInputStream(),
                        request.getFile().getSize()
                )
        );
        log.info("{}: successfully uploaded object ({}) to s3", userContext.getEmail(), filename);

        log.info("Textract Asynchronous Analysis starting file {}", filename);
        StartDocumentAnalysisRequest startRequest =
                StartDocumentAnalysisRequest
                        .builder()
                        .documentLocation(DocumentLocation.builder()
                                .s3Object(S3Object.builder()
                                        .bucket(bucketName)
                                        .name(filename)
                                        .build())
                                .build())
                        .clientRequestToken(UUID.randomUUID().toString())
                        .notificationChannel(
                                NotificationChannel
                                        .builder()
                                        .snsTopicArn(configurationProperties.sns().topic())
                                        .roleArn(configurationProperties.sns().role())
                                        .build()
                        )
                        .featureTypes(FeatureType.TABLES)
                        .jobTag("Statement")
                        .build();

        StartDocumentAnalysisResponse response = textractClient.startDocumentAnalysis(startRequest);
        log.info("{}: Textract jobId: {}", userContext.getEmail(), response.jobId());

        return ResponseEntity.accepted()
                .body(UploadDocumentResponse.builder()
                        .jobId(response.jobId())
                        .build()
                );
    }

    public ResponseEntity<Void> analyseDocument() {
        return ResponseEntity.ok().build();
    }
}
