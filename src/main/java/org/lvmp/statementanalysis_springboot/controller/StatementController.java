package org.lvmp.statementanalysis_springboot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.model.UploadDocumentRequest;
import org.lvmp.statementanalysis_springboot.service.StatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService statementService;

    /**
     * Accepts a multipart request part named "file" and initiates document upload processing.
     *
     * @param request the multipart upload payload bound from the request part named "file"; validated before processing
     * @return a ResponseEntity with no body that represents the outcome of the upload operation
     * @throws IOException if an I/O error occurs while handling the uploaded file
     */
    @PostMapping("/upload-document")
    public ResponseEntity<Void> uploadDocument(@Valid @RequestPart("file") UploadDocumentRequest request) throws IOException {
        return statementService.uploadDocument(request);
    }

    /**
     * Initiates analysis of previously uploaded documents.
     *
     * @return an HTTP response with no body indicating the result of the analysis operation
     */
    @PostMapping("/analyse-document")
    public ResponseEntity<Void> analyseDocument() {
        return statementService.analyseDocument();
    }
}
