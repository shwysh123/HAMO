package com.example.demo.config.auth;

import com.example.demo.domain.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

	private UserDto user;
	//OAuth2----------------------------------------------------------------
	private Map<String,Object> attributes;
	private String accessToken;
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	@Override
	public String getName(){
		return null;
	}
	//OAuth2-----------------------------------------------------------------
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
		Collection<GrantedAuthority> collection = new ArrayList();

		collection.add(new GrantedAuthority(){
			@Override
			public String getAuthority(){
				return user.getRole();
			}
		});
		return collection;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
