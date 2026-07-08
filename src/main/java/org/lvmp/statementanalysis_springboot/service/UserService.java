package org.lvmp.statementanalysis_springboot.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public ResponseEntity<Void> getUser() {
        return ResponseEntity.ok().build();
    }

}
