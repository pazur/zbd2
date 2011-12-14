package dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import content.Content;

@Repository
@Transactional(readOnly = true)
public class ContentDao extends HibernateDaoSupport {
	public List<Content> getAll(){
		DetachedCriteria criteria = DetachedCriteria.forClass(Content.class);
		@SuppressWarnings("unchecked")
		List<Content> contents = getHibernateTemplate().findByCriteria(criteria);
		return contents;
	}
	
	public Content read(Integer id){
		return getHibernateTemplate().load(Content.class, id);	
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Integer save(Content content) {
		Integer res = (Integer) getHibernateTemplate().save(content);
		return res;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void delete(Content content){
		getHibernateTemplate().delete(content);
	}
}
