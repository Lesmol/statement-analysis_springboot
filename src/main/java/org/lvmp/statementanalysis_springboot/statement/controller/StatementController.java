package org.lvmp.statementanalysis_springboot.statement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.statement.dto.request.UploadDocumentRequest;
import org.lvmp.statementanalysis_springboot.statement.dto.response.UploadDocumentResponse;
import org.lvmp.statementanalysis_springboot.statement.service.StatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/statement-analysis")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService statementService;

    @PostMapping("/upload-document")
    public ResponseEntity<UploadDocumentResponse> uploadDocument(@Valid @ModelAttribute UploadDocumentRequest request) throws IOException {
        return statementService.uploadDocument(request);
    }

    @PostMapping("/analyse-document")
    public ResponseEntity<Void> analyseDocument() {
        return statementService.analyseDocument();
    }
}
