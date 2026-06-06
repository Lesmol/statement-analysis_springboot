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

    /**
     * Uploads the provided document to the configured S3 bucket, requests AWS Textract to detect text in the uploaded object, and returns a success response.
     *
     * @param request the request containing the file to upload and analyze
     * @return an HTTP 200 (OK) response with an empty body
     * @throws IOException if reading the provided file's input stream fails
     */
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

    /**
     * Triggers document analysis and returns an empty success response.
     *
     * Currently this method is a stub and does not perform any analysis.
     *
     * @return HTTP 200 OK with an empty body.
     */
    public ResponseEntity<Void> analyseDocument() {
        return ResponseEntity.ok().build();
    }
}
