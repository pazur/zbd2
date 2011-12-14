package dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import content.Announcement;
import daoint.AnnouncementDaoInt;


@Repository
@Transactional(readOnly = true)
public class AnnouncementDao extends HibernateDaoSupport implements AnnouncementDaoInt{
	public Announcement get(Long id){
		return getHibernateTemplate().get(Announcement.class, id);	
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Long save(Announcement announcement) {
		Long res = (Long) getHibernateTemplate().save(announcement);
		return res;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Announcement announcement) {
		getHibernateTemplate().saveOrUpdate(announcement);
	}
	
	@Override
	public List<Announcement> getAll() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Announcement.class);
		@SuppressWarnings("unchecked")
		List<Announcement> announcements = getHibernateTemplate().findByCriteria(criteria);
		return announcements;
	}

}
