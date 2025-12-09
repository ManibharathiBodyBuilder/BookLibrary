package com.booklibrary.securityconfiguration;


import javax.management.relation.Role;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())
	        .headers(headers -> headers.frameOptions().disable())
	        .authorizeHttpRequests(auth -> auth
	            .antMatchers("/css/**","/js/**","/pdfjs/**","/images/**").permitAll()
	            .antMatchers("/login","/register","/forgot-password","/reset-password").permitAll()
	            .antMatchers("/readbook/**","/readbookpage/**").permitAll()
	            .antMatchers("/h2-console/**").permitAll()
	            .antMatchers("/testing").permitAll()    
	            .antMatchers("/deletebook/**").authenticated()
	            .antMatchers(HttpMethod.GET, "/book_register").hasRole("ADMIN")
	            .anyRequest().authenticated()   // 🔥 MUST BE authenticated, not permitAll
	        )
	        .formLogin(form -> form
	            .loginPage("/login")
	            .loginProcessingUrl("/login")      // 🔥 This is the fix!
	            .defaultSuccessUrl("/Verified", true)
	            .failureUrl("/login?error=true")
	            .permitAll()
	        )
	        .logout(logout -> logout.permitAll())
	        .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));

	    return http.build();
	}


	





/*	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf->csrf.disable())
		
		.authorizeHttpRequests(authz -> authz
			    .antMatchers(HttpMethod.GET, "/book_register").authenticated()  // Only this needs login
			    .anyRequest().permitAll()) // Everything else open
			.formLogin()
			.and()
			.exceptionHandling()
			.accessDeniedPage("/access-denied")

			.httpBasic();


		return http.build();

	}*/
	
	@Bean
	public org.springframework.security.core.userdetails.UserDetailsService UserDetailsService(BCryptPasswordEncoder encoder) {
		UserDetails admin = User.withUsername("Manibharathi").password(encoder.encode("787898")).roles("ADMIN").build();
		UserDetails user = User.withUsername("demoUser").password(encoder.encode("demo@2025")).roles("USER").build();
		return new InMemoryUserDetailsManager(user, admin);
		
	//	return new CustomUserDetailsService();

	}
	
/*	@Bean
	public DaoAuthenticationProvider AuthenticationProvider() {
		DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
		daoProvider.setUserDetailsService(UserDetailsService());
		daoProvider.setPasswordEncoder(passwordEncoder());
		return daoProvider;
	}*/
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	 
	
	
	 
}
