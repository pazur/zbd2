package listeners;

import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.springframework.beans.factory.annotation.Autowired;

import content.AnnouncementInstance;

import dao.AclDaoInt;

public class AnnouncementInstancePostInsertListener implements
		PostInsertEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1921937514153745107L;

	@Autowired
	private AclDaoInt aclDao;
	
	@Override
	public void onPostInsert(PostInsertEvent arg0) {
		Object entity = arg0.getEntity();
		if (entity.getClass().equals(AnnouncementInstance.class)){
			AnnouncementInstance ai = (AnnouncementInstance) entity;
			aclDao.addAccess(AnnouncementInstance.class, (Long) arg0.getId(), ai.getReceiver(), 1);
		}
	}
}
