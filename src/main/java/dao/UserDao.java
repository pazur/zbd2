package dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import user.User;

@Repository
@Transactional(readOnly = true)
public class UserDao extends HibernateDaoSupport implements UserDaoInt{
	public User get(Long id){
		return getHibernateTemplate().get(User.class, id);	
	}
	
	public User get(String username){
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		criteria.add(Restrictions.eq("username", username));
		try{
			return (User) getHibernateTemplate().findByCriteria(criteria).get(0);
		} catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Long save(User user) {
		Long res = (Long) getHibernateTemplate().save(user);
		return res;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(User user) {
		getHibernateTemplate().saveOrUpdate(user);
	}
	
	@Override
	public List<User> getAll() {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		@SuppressWarnings("unchecked")
		List<User> users = getHibernateTemplate().findByCriteria(criteria);
		return users;
	}
}
