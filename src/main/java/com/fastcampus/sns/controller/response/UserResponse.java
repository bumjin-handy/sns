package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.User;
import com.fastcampus.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String name;

    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getId(),
                user.getUsername());
    }
}
