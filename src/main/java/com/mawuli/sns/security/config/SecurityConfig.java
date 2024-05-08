package com.mawuli.sns.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mawuli.sns.security.domain.dto.response.AuthenticationResponse;
import com.mawuli.sns.security.services.AuthenticationService;
import com.mawuli.sns.security.services.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${frontend.url}")
    String frontend_url = "http://localhost:4200";

    @Autowired
    private ApplicationContext context;

    @Lazy
    private AuthenticationService authenticationService;

    public AuthenticationService getAuthenticationService() {
        if (authenticationService == null) {
            authenticationService = context.getBean(AuthenticationService.class);
        }
        return authenticationService;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html",
                                "/graphql",
                                "/graphql/**",
                                "/graphiql",
                                "/graphiql/**",
                                "/login",
                                "/public/**",
                                "oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(authorizationEndpointConfig ->
                                authorizationEndpointConfig
                                        .authorizationRequestRepository(new HttpSessionOAuth2AuthorizationRequestRepository())
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                            String name = oauthToken.getPrincipal().getAttribute("name");
                            String email = oauthToken.getPrincipal().getAttribute("email");

                            AuthenticationResponse authResponse = getAuthenticationService().registerOAuthUser(email, name);
                            String jwtToken = authResponse.getToken();
                            String refreshToken = authResponse.getRefreshToken();

                            ObjectMapper mapper = new ObjectMapper();
                            String userJson = mapper.writeValueAsString(authResponse.getUser());
                            String encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8);

                            response.sendRedirect(frontend_url + "?token=" + jwtToken + "&refreshToken=" + refreshToken + "&user=" + encodedUserJson);
                        })
                );

        ;
        return http.build();
    }
}
