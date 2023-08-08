package com.example.modiraa.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음.
// login 요청해서 nickname, password 전송하면 (psot)
// UsernamePasswordAuthenticationFilter 동작을 함.
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtProperties jwtProperties) {
        super(authenticationManager);
        this.jwtProperties = jwtProperties;
    }

    // login 요청을 하면 로그인 시도를 위해서 함수 실행
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 로그인 시도중");
        // 1.nickname, password 받아서
        // 2. 정상인지 로그인 시도를 해보는 것. authenticationManager로 로그인 시도를 하면!!
        // PrincipalDetailsService가 호출 loadUserByUsername() 함수 실행됨
        // PrincipalDetailsService의 loadUserByUsername()함수가 실행된 후 정상이면 authentication이 리턴됨
        // DB에 있는 nickname과 password가 일치
        try {
            ObjectMapper om = new ObjectMapper();
            LoginRequest loginRequest = om.readValue(request.getInputStream(), LoginRequest.class);
            System.out.println(loginRequest); //입력된 값 확인
            System.out.println("==============================================================");

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            Authentication authentication =
                    getAuthenticationManager().authenticate(authenticationToken);

            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // attemptAuthentication실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨.
    // JWT 토큰을 만들어서 request요청한 사용자에게 JWT토큰을 response해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 인증이 완료.");
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

        //JWT 토큰 발급
        String jwtToken = JWT.create()
                .withSubject(jwtProperties.getSubject())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpiration()))
                .withClaim(jwtProperties.getNicknameClaim(), userDetails.getMember().getNickname())
                .sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));

        response.addHeader(jwtProperties.getAccessHeader(), jwtProperties.getTokenPrefix() + jwtToken);
    }

    //로그인 실패시 예외 처리
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println(failed.getMessage());
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(failed.getMessage());
    }
}
