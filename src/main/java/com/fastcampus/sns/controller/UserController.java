package com.fastcampus.sns.controller;

import com.fastcampus.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vi/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // TODO: implement
    @PostMapping("/join")
    public void join() {
        userService.join("", "");
    }
}
