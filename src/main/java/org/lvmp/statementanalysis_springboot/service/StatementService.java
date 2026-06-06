package org.lvmp.statementanalysis_springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.model.UploadDocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.S3Object;

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

    public ResponseEntity<Void> uploadDocument(UploadDocumentRequest request) throws IOException {
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

        Document document = Document.builder()
                .s3Object(S3Object.builder()
                        .bucket(bucketName)
                        .name(filename)
                        .build())
                .build();

        DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
                .document(document)
                .build();

        textractClient.detectDocumentText(detectDocumentTextRequest);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> analyseDocument() {
        return ResponseEntity.ok().build();
    }
}
