package com.example.modiraa.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.MemberRepository;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//시큐리티가 filter 가지고 있는데 그 필터중에 BasicAuthenticationFilter 라는 것이 있음.
//권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있음.!!!!!!
//만약 권한이나 인증이 필요한 주소가 아니라면 이 필터를 안탐.
@Slf4j
@Component
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;
    private final JwtProperties jwtProperties;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, JwtProperties jwtProperties) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(jwtProperties.getAccessHeader());

        if (header == null || !header.startsWith(jwtProperties.getTokenPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        // JWT 토큰을 검증해서 정상적인 사용자인지 확인
        String jwtToken = request.getHeader(jwtProperties.getAccessHeader()).replace(jwtProperties.getTokenPrefix(), "");

        log.info("header {}", header);

        String nickname =
                JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey())).build().verify(jwtToken).getClaim(jwtProperties.getNicknameClaim()).asString();

        // 서명이 정상적으로 됨
        if (nickname != null) {
            UserDetails userDetails = getUserDetailsFromNickname(nickname);
            //Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어 준다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            //홀더에 검증이 완료된 정보 값 넣어준다. -> 이제 controller 에서 @AuthenticationPrincipal UserDetailsImpl userDetails 로 정보를 꺼낼 수 있다.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }

    private UserDetails getUserDetailsFromNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(
                () -> new IllegalArgumentException("nickname이 없습니다.")
        );

        return new UserDetailsImpl(member);
    }

    /**
     * Jwt Token을 복호화 하여 Member를 얻는다.
     */
    public String getNicknameFromJwt(String token) {

        String nickname =
                JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey())).build().verify(token).getClaim(jwtProperties.getNicknameClaim()).asString();

        if (nickname != null) {
            Member memberEntity = memberRepository.findByNickname(nickname).orElseThrow(
                    () -> new IllegalArgumentException("nickname이 없습니다.")
            );
            UserDetailsImpl userDetails = new UserDetailsImpl(memberEntity);

            return userDetails.getMember().getNickname();
        }

        throw new IllegalArgumentException("회원이 아닙니다.");
    }

    public Member getMemberFromJwt(String token) {

        String nickname =
                JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey())).build().verify(token).getClaim(jwtProperties.getNicknameClaim()).asString();

        if (nickname != null) {
            Member memberEntity = memberRepository.findByNickname(nickname).orElseThrow(
                    () -> new IllegalArgumentException("nickname이 없습니다.")
            );
            UserDetailsImpl userDetails = new UserDetailsImpl(memberEntity);

            return userDetails.getMember();
        }

        throw new IllegalArgumentException("회원이 아닙니다.");
    }

    public boolean validateToken(String jwt) {
        return this.getClaims(jwt) != null;
    }

    private Jws<Claims> getClaims(String jwt) {
        try {
            return Jwts.parser().setSigningKey(jwtProperties.getSecretKey().getBytes()).parseClaimsJws(jwt);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
            throw ex;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw ex;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw ex;
        }
    }
}
