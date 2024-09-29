package com.dpide.dpide.user.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String email;
    private String nickname;
    private String password;
}

