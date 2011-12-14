package listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreLoadEvent;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import content.Announcement;
import content.AnnouncementInstance;
import content.Content;

import dao.AclDaoInt;

public class AclListener implements PreLoadEventListener, PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener{

	private static enum AllowState{
		ALLOW,
		DENY,
		NONE
	}
	
	private static final long serialVersionUID = 7673497942043990970L;

	@Autowired
	private AclDaoInt aclDao;
	
	private Set<Class<?>> getAccessControlledClasses(){
		Class<?>[] classes = {Content.class, Announcement.class, AnnouncementInstance.class};
		return new HashSet<Class<?>>(Arrays.asList(classes));
		
	}

	private AllowState exceptions(Class<?> cls){
		if (cls.equals(AnnouncementInstance.class)){
			GrantedAuthority grantedAuthority = new GrantedAuthorityImpl("CREATE_ANNOUNCEMENTINSTANCE");
			if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(grantedAuthority))
				return AllowState.ALLOW;
		}
		return AllowState.NONE;
	}
	
	public boolean allow(Object entity, Long id, Integer minRights){
		Class<?> cls = entity.getClass();
		AllowState a = exceptions(cls);
		if (a.equals(AllowState.ALLOW))
			return true;
		if (a.equals(AllowState.DENY))
			return false;
		if (getAccessControlledClasses().contains(cls)){
			Integer rights = aclDao.checkAcl(cls, id);
			if (rights < minRights)
				return false;
		}
		return true;
	}
	
	@Override
	public void onPreLoad(PreLoadEvent arg0) throws CallbackException{
		if (!allow(arg0.getEntity(), (Long) arg0.getId(), 0))
			throw new CallbackException("load permission denied");
			
	}

	@Override
	public boolean onPreInsert(PreInsertEvent arg0) {
		if(!allow(arg0.getEntity(), null, 1))
			throw new CallbackException("insert permission denied");
		return false;
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent arg0) {
		if(!allow(arg0.getEntity(), (Long) arg0.getId(), 1))
			throw new CallbackException("update permission denied");
		return false;
	}

	@Override
	public boolean onPreDelete(PreDeleteEvent arg0) {
		if(!allow(arg0.getEntity(), (Long) arg0.getId(), 1))
			throw new CallbackException("delete permission denied");
		aclDao.removeAclsFor(arg0.getEntity().getClass(), (Long) arg0.getId());
		return false;
	}
}
