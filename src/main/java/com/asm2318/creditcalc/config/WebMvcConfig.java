package com.asm2318.creditcalc.config;

import com.asm2318.creditcalc.utils.FormName;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer{
    
    @Override
    public void addViewControllers(final ViewControllerRegistry registry){
        registry.addViewController("/loginForm").setViewName(FormName.LOGIN_PAGE);
    }
}
