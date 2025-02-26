package com.kcb.recon.tool.authentication.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${cors.origin}")
    private String origin;

    private final JwtAuthenticationFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(
                                "/api/v1/Auth/**",
                                        "/api/security/**",
                                        "/api/v1/Menus/**",
                                        "/api/v1/SubMenus/**",
                                        "/api/v1/Bulk/**",
                                        "/api/v1/Download/**",
                                        "/api/v1/Agents/**",
                                        "/api/v1/Devices/**",
                                        "/swagger-ui/**",
                                        "/api/v1/Regions/FindByCountries",
                                        "/api-docs/**",
                                        "/api/v1/partners/**",
                                        "/api/v1/partnersConfig/**",
                                        "/api/v1/projects/**",
                                        "/api/v1/Levels/**",
                                        "/api/v1/programs/**",
                                        "/api/v1/Devices/**",
                                        "/api/v1/config/**",
                                        "/api/v1/PartnerTypes/**",
                                        "/api/v1/LocationMapping/**",
                                        "/api/v1/beneficiaries/**",
                                        "/api/v1/Integrations/**",
                                        "api/v1/Configs/**")
                                        .permitAll()
                                        //"/api/v1/partners/**",
                                        //"/api/v1/partnersConfig/**",
                                        //"/api/v1/projects/**",
                                        //"/api/v1/Levels/**",
                                        //"/api/v1/programs/**",
                                        //"/api/v1/Devices/**",
                                        //"/api/v1/PartnerTypes/**")

                                .anyRequest().authenticated()
                ).sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
                //.formLogin(AbstractHttpConfigurer::disable)
                //.httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origin)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(origin);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
