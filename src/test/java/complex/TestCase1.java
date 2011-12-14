package complex;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import content.Announcement;
import content.AnnouncementInstance;
import content.Content;

import auth.MyAuthentication;

import dao.AclDaoInt;
import dao.AnnouncementDaoInt;
import dao.AnnouncementInstanceDaoInt;
import dao.GroupDaoInt;
import dao.UserDaoInt;

import services.AnnouncementService;
import user.Group;
import user.User;

@ContextConfiguration(locations = { "classpath:testContext.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCase1 {

	@Autowired
	private UserDaoInt userDao;
	
	@Autowired
	private GroupDaoInt groupDao;
	
	@Autowired
	private MyAuthentication auth;
	
	@Autowired
	private AclDaoInt aclDao;
	
	@Autowired
	private AnnouncementService announcementService;
	
	@Autowired
	private AnnouncementInstanceDaoInt announcementInstanceDao;
	
	@Autowired
	private AnnouncementDaoInt announcementDao;
	
	private User createUser(String username){
		User user = new User();
		user.setUsername(username);
		user.setGroups(new HashSet<Group>());
		userDao.save(user);
		return user;
	}
	private Group createGroup(String name){
		Group group = new Group();
		group.setName(name);
		groupDao.save(group);
		return group;
		
	}
	
	/*
	 * 1) Create two users;
	 * 2) Sender sends announcement to both
	 * 3) Sender reads announcement instance
	 * 4) Sender tries to create announcement instance (fails)
	 * 5) Receiver changes instance
	 * 6) Receiver tries to change announcement (fails)
	 */
	@Test
	public void test1(){
		//1
		User sender = createUser("sender");
		User receiver = createUser("receiver");
		//2
		auth.login("sender");
		aclDao.addAccess(Announcement.class, null, sender, 1);
		Announcement announcement = new Announcement();
		announcement.setBody("body");
		announcement.setTitle("title");
		announcementService.sendAnnouncement(announcement, Arrays.asList(sender, receiver));
		//3
		List<AnnouncementInstance> unread = announcementInstanceDao.getUnread();
		assertEquals(1, unread.size());
		announcementInstanceDao.read(unread.get(0));
		unread = announcementInstanceDao.getUnread();
		assertEquals(0, unread.size());
		//4
		try{
			AnnouncementInstance ai = new AnnouncementInstance();
			ai.setAnnouncement(announcement);
			ai.setReadDate(new Date(3));
			ai.setReadStatus(false);
			ai.setReceiver(receiver);
			announcementInstanceDao.save(ai);
			fail();
		} catch (HibernateSystemException c){}
		//5
		auth.login("receiver");
		unread = announcementInstanceDao.getUnread();
		assertEquals(1, unread.size());
		AnnouncementInstance ai = unread.get(0);
		ai.setReadStatus(true);
		announcementInstanceDao.update(ai);
		//6
		Announcement an = ai.getAnnouncement();
		an.setTitle("title2");
		try{
			announcementDao.saveOrUpdate(an);
			fail();
		} catch (HibernateSystemException c){}
	}
	
	/*
	 * 1) Create 3 groups
	 * 2) Create some users and add to groups
	 * 3) Send announcements to 2 groups
	 * 4) Check if not send twice
	 */
	@Test
	public void test2(){
		// 1&&2
		Group g1 = createGroup("group1");
		Group g2 = createGroup("group2");
		Group g3 = createGroup("group3"); //sender - not receiver
		User u1 = createUser("test21");
		User u2 = createUser("test22");
		User u3 = createUser("test23");
		User u4 = createUser("test24");
		User u5 = createUser("test25");
		u5.getGroups().add(g3); 
		u1.getGroups().add(g1);
		u2.getGroups().add(g2);
		u3.getGroups().addAll(Arrays.asList(g1, g2));
		u4.getGroups().addAll(Arrays.asList(g1, g2, g3));
		userDao.saveOrUpdate(u1);
		userDao.saveOrUpdate(u2);
		userDao.saveOrUpdate(u3);
		userDao.saveOrUpdate(u4);
		userDao.saveOrUpdate(u5);
		g1 = groupDao.get(g1.getId());
		g2 = groupDao.get(g2.getId());
		g3 = groupDao.get(g3.getId());
		
		// 3
		auth.login("test25");
		aclDao.addAccess(Announcement.class, null, u5, 1);
		Announcement announcement = new Announcement();
		announcement.setBody("bodybody");
		announcement.setTitle("titletitle");
		announcementService.sendAnnouncementToGroups(announcement, Arrays.asList(g1, g2));
		
		//4
		auth.login("test21");
		readAnnouncement(1);
		auth.login("test22");
		readAnnouncement(1);
		auth.login("test23");
		readAnnouncement(1);
		auth.login("test24");
		readAnnouncement(1);
		auth.login("test25");
		readAnnouncement(0);
	}
	
	private void readAnnouncement(int expected){
		List<AnnouncementInstance> announcements = announcementInstanceDao.getUnread();
		assertEquals(expected, announcementInstanceDao.getUnread().size());
		for(AnnouncementInstance instance: announcements){
			instance.getAnnouncement().getTitle();
		}
	}
	
	/*
	 * Test adding rights for user/group
	 */
	@Test
	public void test3(){
		User u = createUser("test3user");
		Group g1 = createGroup("group13");
		Group g2 = createGroup("group23");
		u.getGroups().add(g1);
		u.getGroups().add(g2);
		userDao.saveOrUpdate(u);
		groupDao.saveOrUpdate(g1);
		groupDao.saveOrUpdate(g2);
		g1 = groupDao.get(g1.getId());
		g2 = groupDao.get(g2.getId());
		
		auth.login("test3user");
		aclDao.addAccess(Announcement.class, 1L, u, 0);
		assertEquals(0, aclDao.checkAcl(Announcement.class, 1L)); //FAILS
		assertEquals(-1, aclDao.checkAcl(Content.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, null));
		assertEquals(-1, aclDao.checkAcl(Content.class, null));
		
		aclDao.addAccess(Announcement.class, 1L, g1, 1);
		assertEquals(1, aclDao.checkAcl(Announcement.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Content.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, null));
		assertEquals(-1, aclDao.checkAcl(Content.class, null));	
		
		aclDao.addAccess(Content.class, 2L, g2, 0);
		assertEquals(0, aclDao.checkAcl(Content.class, 2L));
		assertEquals(1, aclDao.checkAcl(Announcement.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, 2L));
		assertEquals(-1, aclDao.checkAcl(Content.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, null));
		assertEquals(-1, aclDao.checkAcl(Content.class, null));	
	
		aclDao.revokeAccess(Announcement.class, null, u);
		assertEquals(0, aclDao.checkAcl(Content.class, 2L));
		assertEquals(1, aclDao.checkAcl(Announcement.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Content.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, null));
		assertEquals(-1, aclDao.checkAcl(Content.class, null));

		aclDao.revokeAccess(Announcement.class, 1L, u);
		assertEquals(0, aclDao.checkAcl(Content.class, 2L));
		assertEquals(1, aclDao.checkAcl(Announcement.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Content.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, null));
		assertEquals(-1, aclDao.checkAcl(Content.class, null));

		aclDao.revokeAccess(Announcement.class, 1L, u);
		assertEquals(0, aclDao.checkAcl(Content.class, 2L));
		assertEquals(1, aclDao.checkAcl(Announcement.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Content.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, null));
		assertEquals(-1, aclDao.checkAcl(Content.class, null));
		

		aclDao.revokeAccess(Announcement.class, 1L, g1);
		assertEquals(0, aclDao.checkAcl(Content.class, 2L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Content.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, null));
		assertEquals(-1, aclDao.checkAcl(Content.class, null));
		
		aclDao.addAccess(Content.class, 2L, g1, 1);
		assertEquals(1, aclDao.checkAcl(Content.class, 2L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, 2L));
		assertEquals(-1, aclDao.checkAcl(Content.class, 1L));
		assertEquals(-1, aclDao.checkAcl(Announcement.class, null));
		assertEquals(-1, aclDao.checkAcl(Content.class, null));	
	}
}
