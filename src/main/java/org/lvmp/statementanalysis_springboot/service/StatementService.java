package org.lvmp.statementanalysis_springboot.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.model.UploadDocumentRequest;
import org.lvmp.statementanalysis_springboot.model.UploadDocumentResponse;
import org.springframework.beans.factory.annotation.Value;
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
    private final TextractClient textractClient;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.sns.topic}")
    private String snsTopicArn;

    @Value("${aws.sns.role}")
    private String roleArn;

    public ResponseEntity<UploadDocumentResponse> uploadDocument(UploadDocumentRequest request) throws IOException {
        String filename = UUID.randomUUID().toString();

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
        log.info("Successfully uploaded object ({}) to s3", filename);

        StartDocumentAnalysisRequest startRequest =
                StartDocumentAnalysisRequest
                        .builder()
                        .documentLocation(DocumentLocation.builder()
                                .s3Object(S3Object.builder()
                                        .bucket(bucketName)
                                        .name(filename)
                                        .build())
                                .build())
                        .clientRequestToken(filename)
                        .notificationChannel(
                                NotificationChannel
                                        .builder()
                                        .snsTopicArn(snsTopicArn)
                                        .roleArn(roleArn)
                                        .build()
                        )
                        .featureTypes(FeatureType.TABLES)
                        .jobTag("Statement")
                        .build();

        StartDocumentAnalysisResponse response = textractClient.startDocumentAnalysis(startRequest);
        log.info("Textract jobId: {}", response.jobId());

        return ResponseEntity.accepted()
                .body(UploadDocumentResponse.builder()
                        .jobId(response.jobId())
                        .build()
                );
    }

    @PostConstruct
    public void validateSnsConfig() {
        if (snsTopicArn == null || snsTopicArn.isBlank()) {
            throw new IllegalStateException("snsTopicArn must not be blank");
        }
        if (roleArn == null || roleArn.isBlank()) {
            throw new IllegalStateException("roleArn must not be blank");
        }
    }

    public ResponseEntity<Void> analyseDocument() {
        return ResponseEntity.ok().build();
    }
}
