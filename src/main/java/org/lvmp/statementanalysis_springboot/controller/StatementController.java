package org.lvmp.statementanalysis_springboot.controller;

import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.service.StatementService;
import org.lvmp.statementanalysis_springboot.validation.PdfFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService statementService;

    @PostMapping("/upload-document")
    public ResponseEntity<Void> uploadDocument(@PdfFile @RequestPart(value = "file") MultipartFile request) throws IOException {
        return statementService.uploadDocument(request);
    }

    @PostMapping("/analyse-document")
    public ResponseEntity<Void> analyseDocument() {
        return statementService.analyseDocument();
    }
}
