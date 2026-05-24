package org.lvmp.statementanalysis_springboot.controller;

import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.service.StatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService statementService;

    @PostMapping("/upload-document")
    public ResponseEntity<Void> uploadDocument() {
        return statementService.uploadDocument();
    }

    @PostMapping("/analyse-document")
    public ResponseEntity<Void> analyseDocument() {
        return statementService.analyseDocument();
    }
}
