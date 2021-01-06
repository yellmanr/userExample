package com.yellman.example.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
         .passwordEncoder(encoder)
         .withUser("sam").password(encoder.encode("sam")).roles("VIEWER")
         .and().withUser("carlos").password(encoder.encode("carlos")).roles("VIEWER", "EDITOR")
         .and().withUser("john").password(encoder.encode("john")).roles("VIEWER", "EDITOR");
    }
	
	protected void configure(final HttpSecurity http) throws Exception {
		http
//  To work with Postman needed this line.  Postman can be adjusted to 
		    .csrf().disable()
		    .antMatcher("/**")
		    .authorizeRequests()
		    .anyRequest().authenticated()
		    .and().httpBasic()
		    ;
	}
}