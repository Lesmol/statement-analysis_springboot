package org.lvmp.statementanalysis_springboot.user;

import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.user.internal.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getUser() {
        return userService.getUser();
    }
}
