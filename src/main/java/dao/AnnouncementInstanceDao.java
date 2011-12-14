package dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import auth.MyAuthentication;

import user.User;

import content.AnnouncementInstance;

@Repository
@Transactional(readOnly = true)
public class AnnouncementInstanceDao extends HibernateDaoSupport implements AnnouncementInstanceDaoInt {
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveAll(Collection<AnnouncementInstance> ais){
		getHibernateTemplate().saveOrUpdateAll(ais);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void save(AnnouncementInstance ai){
		getHibernateTemplate().save(ai);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void update(AnnouncementInstance ai){
		getHibernateTemplate().update(ai);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void delete(AnnouncementInstance ai){
		getHibernateTemplate().delete(ai);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<AnnouncementInstance> getUnread(){
		User currentUser = MyAuthentication.getCurrentUser();
		return getHibernateTemplate().find("from AnnouncementInstance where receiver = ? and readStatus = ?", currentUser, false);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void read(AnnouncementInstance ai){
		ai.setReadStatus(true);
		ai.setReadDate(new Date());
		update(ai);
	}
}
