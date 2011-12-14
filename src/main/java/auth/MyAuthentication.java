package auth;


import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;


import dao.UserDaoInt;

import user.User;

public class MyAuthentication {
	
	@Autowired
	private UserDaoInt userDao;
	
	public void login(String username){
		AuthenticationManager am = new SampleAuthenticationManager(userDao);
		Authentication request = new UsernamePasswordAuthenticationToken(username, "");
		Authentication result = am.authenticate(request);
		SecurityContextHolder.getContext().setAuthentication(result);
	}
	public void logout(String username){
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	public static User getCurrentUser(){
		return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	public static void addAuthority(GrantedAuthority authority){
		User user = getCurrentUser();
		Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
		List<GrantedAuthority> authorities =
				new ArrayList<GrantedAuthority>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		authorities.add(authority);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, credentials, authorities)
		);
	}
	
	public static void removeAuthority(GrantedAuthority authority){
		User user = getCurrentUser();
		Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
		List<GrantedAuthority> authorities =
				new ArrayList<GrantedAuthority>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		authorities.remove(authority);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, credentials, authorities)
		);
	}
}
