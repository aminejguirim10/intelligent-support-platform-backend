package intership.intelligentsupportplatformbackend.security.config;





import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationProvider;

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



import java.util.Arrays;

import java.util.List;



/*

 * Main Security Configuration class.

 * Configures HTTP security, CORS, session management, and request authorization.

 */

@Configuration

@EnableWebSecurity

@EnableMethodSecurity(prePostEnabled = true)

@RequiredArgsConstructor

public class SecurityConfig {



    private final JwtAuthenticationFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;



    /*

     * Whitelist of endpoints that don't require authentication.

     */

    private static final String[] WHITE_LIST_URL = {

            "/api/health",

            "/public/**",

            "/auth/register",

            "/auth/login",

            "/auth/refresh-token",

            "/auth/forgot-password",

            "/auth/reset-password",

            "/v3/api-docs",

            "/v3/api-docs/**",

            "/swagger-resources",

            "/swagger-resources/**",

            "/configuration/ui",

            "/configuration/security",

            "/swagger-ui/**",

            "/webjars/**",

            "/swagger-ui.html"

    };



    /*

     * Configure the security filter chain.

     */

    @Bean

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                // Disable CSRF for stateless API

                .csrf(AbstractHttpConfigurer::disable)



                // Configure CORS

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure authorization rules

                .authorizeHttpRequests(auth -> auth

                        // Public endpoints - no authentication required

                        .requestMatchers(WHITE_LIST_URL).permitAll()



                        // Admin-only registration routes

                        //.requestMatchers(HttpMethod.POST, "/auth/register", "/auth/register-admin").hasRole("ADMIN")

                        // All other requests require authentication

                        .anyRequest().authenticated()

                )

                // Configure stateless session management (no session cookies)

                .sessionManagement(session -> session

                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                )



                // Set custom authentication provider

                .authenticationProvider(authenticationProvider)



                // Add JWT filter before UsernamePasswordAuthenticationFilter

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);







        return http.build();

    }



    /*

     * Configure CORS settings.

     * Allows cross-origin requests from specified origins.

     */

    @Bean

    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(

                "http://localhost:3000",

                "http://localhost:4200",

                "http://localhost:5173"

        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setExposedHeaders(List.of("Authorization"));

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);



        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;

    }

}

