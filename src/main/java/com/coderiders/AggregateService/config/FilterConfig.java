package com.coderiders.AggregateService.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtDecodingFilter jwtDecodingFilter;

    @Bean
    public FilterRegistrationBean<JwtDecodingFilter> jwtDecodingFilterRegistrationBean() {
        FilterRegistrationBean<JwtDecodingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtDecodingFilter);
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}