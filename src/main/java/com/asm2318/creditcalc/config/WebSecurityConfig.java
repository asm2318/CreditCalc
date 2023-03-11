package com.asm2318.creditcalc.config;

import com.asm2318.creditcalc.enums.Authority;
import com.asm2318.creditcalc.services.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
       
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
    
    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .authorizeRequests()
                .antMatchers("/css/**", "/registration", "/").permitAll()
                .antMatchers("/adminForm", "/usersList", "/roleUpdate", "/requestsHistory")
                    .hasAuthority(Authority.ADMIN.name())
                .antMatchers("/calculation", "/calculationForm", "/calculationNew").hasAuthority(Authority.USER.name())
                .antMatchers("/userPage").authenticated()
                .anyRequest().denyAll()
                
                .and().formLogin()
                .loginPage("/loginForm")
                .permitAll()
                .defaultSuccessUrl("/userPage", true)
                
                .and().csrf().disable();
    }
    
}
