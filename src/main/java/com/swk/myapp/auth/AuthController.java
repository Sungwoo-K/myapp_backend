package com.swk.myapp.auth;

import com.swk.myapp.auth.entity.Login;
import com.swk.myapp.auth.entity.LoginRepository;
import com.swk.myapp.auth.request.SignupRequest;
import com.swk.myapp.auth.util.HashUtil;
import com.swk.myapp.auth.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private LoginRepository repo;

    @Autowired
    private AuthService service;

    @Autowired
    private HashUtil hash;

    @Autowired
    private JwtUtil jwt;

    @PostMapping(value = "/signup")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody SignupRequest req) {

        Optional<Login> findEmail = repo.findByEmail(req.getEmail());
        Optional<Login> findNickname = repo.findByNickname(req.getNickname());

        if(findEmail.isPresent()) {
            Map<String,String> res = new HashMap<>();
            res.put("message", "이미 존재하는 이메일입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        }

        if(findNickname.isPresent()) {
            Map<String,String> res = new HashMap<>();
            res.put("message", "이미 존재하는 별명입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        }

        service.createIdentity(req);

        Map<String,String> res = new HashMap<>();
        res.put("message", "CheersCraft의 회원이 되신 것을 환영합니다!");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping(value = "/signin")
    public ResponseEntity signIn(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse res) {
        Optional<Login> login = repo.findByEmail(email);
        if(!login.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(ServletUriComponentsBuilder
                            .fromHttpUrl("http://localhost:5500/view/sign-in.html?err=Unauthorized")
                            .build().toUri())
                    .build();
        }

        boolean isVerified = hash.verifyHash(password, login.get().getSecret());

        if(!isVerified) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(ServletUriComponentsBuilder
                            .fromHttpUrl("http://localhost:5500/view/sign-in.html?err=Unauthorized")
                            .build().toUri())
                    .build();
        }

        Login l = login.get();

        String token = jwt.createToken(
                l.getId(), l.getEmail(),
                l.getNickname());

        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwt.TOKEN_TIMEOUT/ 1000L));
        cookie.setDomain("localhost");


        res.addCookie(cookie);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(ServletUriComponentsBuilder
                        .fromHttpUrl("http://localhost:5500/view/review-page.html")
                        .build().toUri())
                .build();
    }
}
