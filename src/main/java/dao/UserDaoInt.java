package dao;


import java.util.List;

import user.User;

public interface UserDaoInt {
	public User get(Long id);
	public User get(String username);
	public Long save(User user);
	public List<User> getAll();
	public void saveOrUpdate(User user);
}
