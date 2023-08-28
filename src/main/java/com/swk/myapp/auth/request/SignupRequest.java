package com.swk.myapp.auth.request;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String nickname;
}
