package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import acl.Rights;
import auth.MyAuthentication;

import content.Announcement;
import content.AnnouncementInstance;

import user.Group;
import user.User;

import daoint.AclDaoInt;
import daoint.AnnouncementDaoInt;
import daoint.AnnouncementInstanceDaoInt;

public class AnnouncementService {

	@Autowired
	private AnnouncementDaoInt announcementDao;
	
	@Autowired
	private AclDaoInt aclDao;
	
	@Autowired
	private AnnouncementInstanceDaoInt announcementInstanceDao;
	
	private Collection<User> getUsers(Collection<Group> groups){
		Set<User> result = new HashSet<User>();
		Set<Long> ids = new HashSet<Long>();
		ids.add(null);
		for(Group g: groups){
			for(User u: g.getUsers()){
				if (!ids.contains(u.getId())){
					result.add(u);
					ids.add(u.getId());
				}
			}
		}
		return result;
	}
	
	private void createAnnouncementInstances(Announcement announcement, Collection<User> users){
		List<AnnouncementInstance> announcementInstances = new ArrayList<AnnouncementInstance>();
		for(User u:users){
			AnnouncementInstance announcementInstance = new AnnouncementInstance();
			announcementInstance.setAnnouncement(announcement);
			announcementInstance.setReadStatus(false);
			announcementInstance.setReceiver(u);
			announcementInstances.add(announcementInstance);
		}
		GrantedAuthority authority = new GrantedAuthorityImpl("CREATE_ANNOUNCEMENTINSTANCE");
		MyAuthentication.addAuthority(authority);
		announcementInstanceDao.saveAll(announcementInstances);
		MyAuthentication.removeAuthority(authority);
	}
	
	private void addReadRights(Long id, Collection<User> users){
		for(User u: users){
			aclDao.addAccess(Announcement.class, id, u, Rights.READ);
		}
	}
	
	public void sendAnnouncement(Announcement announcement, Collection<User> users){
		Long id = announcementDao.save(announcement);
		addReadRights(id, users);
		createAnnouncementInstances(announcement, users);
	}
	
	public void sendAnnouncementToGroups(Announcement announcement, Collection<Group> groups){
		Long id = announcementDao.save(announcement);
		Collection<User> users = getUsers(groups);
		addReadRights(id, users);
		createAnnouncementInstances(announcement, users);
	}
}
