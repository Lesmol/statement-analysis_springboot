package org.lvmp.statementanalysis_springboot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.context.UserContext;
import org.lvmp.statementanalysis_springboot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserContext userContext;

    @GetMapping
    public ResponseEntity<UserContext> getUser() {
        return ResponseEntity.ok(userContext);
    }
}
