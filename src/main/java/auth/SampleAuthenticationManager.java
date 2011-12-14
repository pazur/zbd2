package auth;

import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import user.User;
import daoint.UserDaoInt;

public class SampleAuthenticationManager implements AuthenticationManager {

	private UserDaoInt userDao;
	
	public SampleAuthenticationManager(UserDaoInt userDao){
		this.userDao = userDao;
	}
  	public Authentication authenticate(Authentication auth) throws AuthenticationException {
  		User user = userDao.get(auth.getName());
  		if (user == null)
  			throw new UsernameNotFoundException(auth.getName());
  		return new UsernamePasswordAuthenticationToken(user,
  				auth.getCredentials(), new ArrayList<GrantedAuthority>());
  	}
}