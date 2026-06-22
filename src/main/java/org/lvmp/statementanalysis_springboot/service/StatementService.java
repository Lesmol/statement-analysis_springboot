package org.lvmp.statementanalysis_springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.model.UploadDocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
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

    public ResponseEntity<Void> uploadDocument(MultipartFile request) throws IOException {
        String filename = UUID.randomUUID().toString();

        boolean fileUploaded = uploadStatement(request,filename);

        if  (fileUploaded) {

            String jobId = initiateTextract(filename);

            // Still need to configure SNS

            textractClient.close();
        }

        return ResponseEntity.ok().build();
    }


    private String initiateTextract(String filename) {
        StartDocumentTextDetectionResponse response = null;
        String jobId = "";

        try {

            S3Object s3Object = S3Object
            .builder()
            .bucket(bucketName)
            .name(filename)
            .build();

            DocumentLocation document = DocumentLocation
                    .builder()
                    .s3Object(s3Object)
                    .build();

            NotificationChannel sns = NotificationChannel
                    .builder()
                    .snsTopicArn(snsTopicArn)
                    .roleArn(roleArn)
                    .build();

            StartDocumentTextDetectionRequest textDetectionRequest =
                    StartDocumentTextDetectionRequest
                            .builder()
                            .documentLocation(document)
                            .clientRequestToken(filename)
                            .notificationChannel(sns)
                            .jobTag("Statement")
                            .build();

            response = textractClient.startDocumentTextDetection(textDetectionRequest);

            jobId = !response.jobId().isEmpty()
                    ? response.jobId() : "";


        } catch (AwsServiceException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return jobId;
    }


    private boolean uploadStatement(MultipartFile request, String filename) {
        try {
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .contentType(request.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(
                            request.getInputStream(),
                            request.getSize()
                    )
            );

        } catch (AwsServiceException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return true;
    }

    public ResponseEntity<Void> analyseDocument() {
        return ResponseEntity.ok().build();
    }
}
