package com.example.modiraa.config;

import com.example.modiraa.config.jwt.JwtAuthenticationFilter;
import com.example.modiraa.config.jwt.JwtAuthorizationFilter;
import com.example.modiraa.config.jwt.JwtProperties;
import com.example.modiraa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final MemberRepository memberRepository;
    private final JwtProperties jwtProperties;

    @Bean   // 비밀번호 암호화
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 정적 자원에 대해서는 Security 설정을 적용하지 않음.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().configurationSource(corsConfigurationSource());
        http.headers().frameOptions().disable(); //h2-console 보기
        http.authorizeRequests()

                // api 요청 접근허용
                .antMatchers("/api/user/**").permitAll()
                .antMatchers("/auth/kakao/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/chat/**").permitAll()
                .antMatchers("**").permitAll()
                .antMatchers("/").authenticated()
                //.antMatchers(HttpMethod.GET,"/api/contents").permitAll()  //GET 요청 허용
                //.antMatchers(HttpMethod.GET, "/api/reply/**").permitAll()

                // 그 외 모든 요청권한
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                // 토큰을 활용하면 세션이 필요 없으므로 STATELESS로 설정하여 Session을 사용하지 않는다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtProperties), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager(authenticationConfiguration), memberRepository, jwtProperties), UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }


    //CORS를 위한 Bean 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 수정 필요
        configuration.addAllowedOrigin("http://modira.co.kr");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("Authorization");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}