package com.example.demo.config;



//security-context.xml 설정 내용

import com.example.demo.config.auth.PrincipalDetailsOAuth2Service;
import com.example.demo.config.auth.PrincipalDetailsService;
import com.example.demo.config.auth.exceptionhandler.CustomAccessDeniedHandler;
import com.example.demo.config.auth.exceptionhandler.CustomAuthenticationEntryPoint;
import com.example.demo.config.auth.loginHandler.CustomAuthenticationFailureHandler;
import com.example.demo.config.auth.loginHandler.CustomLoginSuccessHandler;
import com.example.demo.config.auth.logoutHandler.CustomLogoutHandler;
import com.example.demo.config.auth.logoutHandler.OAuthLogoutHandler;
import com.example.demo.config.auth.logoutHandler.CustomLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private PrincipalDetailsService principalDetailsService;

	@Autowired
	private PrincipalDetailsOAuth2Service principalDetailsOAuth2Service;

	//----------------------------------------------------------------
	// 웹 요청 처리
	//----------------------------------------------------------------
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();

		http
				.authorizeRequests()
				.antMatchers("/","/public","/login","/join","/checkDuplicate",
						"/checkNicknameDuplicate","/findid","/resetpw","/emailcheck",
						"/checkPhoneDuplicate","/sendemail","/checkcode","/draw","/button").permitAll()
				.antMatchers("/css/**","/js/**","/images/**").permitAll()
				.antMatchers("/user").hasRole("USER")				//ROLE_USER
				.antMatchers("/admin").hasRole("ADMIN")				//ROLE_ADMIN
				.anyRequest().authenticated()									//나머지 URL은 모두 인증작업이 완료된 이후 접근가능
				.and()

				//로그인
				.formLogin()
				.loginPage("/login")
				.successHandler(new CustomLoginSuccessHandler())
				.failureHandler(new CustomAuthenticationFailureHandler())

				.and()
				//로그아웃
				.logout()
				.logoutUrl("/logout")
				.permitAll()
				.addLogoutHandler(new OAuthLogoutHandler())
				.logoutSuccessHandler(new CustomLogoutSuccessHandler())

				.and()
				//예외처리
				.exceptionHandling()
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint())      //권한 실패 예외처리
				.accessDeniedHandler(new CustomAccessDeniedHandler())

				//REMEMBER ME
				.and()
				.rememberMe()
				.rememberMeParameter("remember-me")
				.tokenValiditySeconds(60*60)
				.alwaysRemember(false)
				.tokenRepository(tokenRepository())
				.userDetailsService(principalDetailsOAuth2Service)

				.and()
				//OAUTTH2
				.oauth2Login()
				.userInfoEndpoint()
				.userService(principalDetailsOAuth2Service);
	}

	//--------------------------------
	//인층처리 함수
	//--------------------------------
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{

		auth.userDetailsService(principalDetailsOAuth2Service)
				.passwordEncoder(passwordEncoder());

	}
	//--------------------------------
	//BAENS
	//--------------------------------

	//BCryotPasswordEncoder Bean 등록
	//패스워드 검증에 사용
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();

	}

	//REMEMBER ME BEAN 추가
	@Bean
	public PersistentTokenRepository tokenRepository() {

		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		return repo;
	}

}