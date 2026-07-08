package org.lvmp.statementanalysis_springboot.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/user/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> getUser(HttpServletRequest request) {
        Map<String, String> headers = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));
        log.info("Request headers: {}", headers);
        return ResponseEntity.ok(headers);
    }
}
