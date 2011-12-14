package dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import user.Group;

@Repository
@Transactional(readOnly = true)
public class GroupDao extends HibernateDaoSupport implements GroupDaoInt{
	public Group get(Long id){
		return getHibernateTemplate().get(Group.class, id);	
	}
	
	public Group get(String name){
		DetachedCriteria criteria = DetachedCriteria.forClass(Group.class);
		criteria.add(Restrictions.eq("name", name));
		return (Group) getHibernateTemplate().findByCriteria(criteria).get(0);
	}
		
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Long save(Group group) {
		Long res = (Long) getHibernateTemplate().save(group);
		return res;
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Group group) {
		getHibernateTemplate().saveOrUpdate(group);
	}
	
	@Override
	public List<Group> getAll() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Group.class);
		@SuppressWarnings("unchecked")
		List<Group> groups = getHibernateTemplate().findByCriteria(criteria);
		return groups;

	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAll(){
		for(Group g: getAll())
			getHibernateTemplate().delete(g);
	}
}
