package com.example.FitnessCenterApp.config;

import com.example.FitnessCenterApp.filter.JwtAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
@Profile("!test")
@ConditionalOnProperty(name = "auth.enabled", havingValue = "true", matchIfMissing = true)
public class JwtSecurityConfig implements WebMvcConfigurer {

    private final JwtAuthenticationFilter jwtFilter;

    public JwtSecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    @ConditionalOnProperty(name = "auth.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}

