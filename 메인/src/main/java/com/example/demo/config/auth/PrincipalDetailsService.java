package com.example.demo.config.auth;

import com.example.demo.domain.dto.UserDto;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrincipalDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Optional<User> user = userRepository.findById(email);

		if (user.isEmpty())
			return null;

		UserDto dto = new UserDto();
		dto.setEmail(user.get().getEmail());
		dto.setPassword(user.get().getPassword());
		dto.setNickname(user.get().getNickname());
		dto.setName(user.get().getName());
		dto.setBirth(user.get().getBirth());
		dto.setZipcode(user.get().getZipcode());
		dto.setAddr1(user.get().getAddr1());
		dto.setAddr2(user.get().getAddr2());
		dto.setRole(user.get().getRole());
		dto.setPhone(user.get().getPhone());
		dto.setQuestion(user.get().getQuestion());
		dto.setAnswer(user.get().getAnswer());

		PrincipalDetails principalDetails = new PrincipalDetails();
		principalDetails.setUser(dto);

		return principalDetails;

	}

}
