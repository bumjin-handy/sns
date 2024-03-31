package com.fastcampus.sns.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class PostCommentRequest {
    String comment;
}
