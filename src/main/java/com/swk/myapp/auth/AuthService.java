package com.swk.myapp.auth;

import com.swk.myapp.auth.entity.Login;
import com.swk.myapp.auth.entity.LoginRepository;
import com.swk.myapp.auth.request.SignupRequest;
import com.swk.myapp.auth.util.HashUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private LoginRepository repo;

    @Autowired
    private HashUtil hash;

    @Autowired
    public AuthService(LoginRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void createIdentity(SignupRequest req) {
        Login toSaveLogin =
            Login.builder()
                .email(req.getEmail())
                .secret(hash.createHash(req.getPassword()))
                .nickname(req.getNickname())
                .build();
        repo.save(toSaveLogin);
    }
}
