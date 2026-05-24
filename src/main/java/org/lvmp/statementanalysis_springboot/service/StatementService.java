package org.lvmp.statementanalysis_springboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatementService {

    public ResponseEntity<Void> uploadDocument() {
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> analyseDocument() {
        return ResponseEntity.ok().build();
    }
}
