package context;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import content.Announcement;
import content.AnnouncementInstance;

import services.AnnouncementService;
import user.Group;
import user.User;

import daoint.AclDaoInt;
import daoint.AnnouncementInstanceDaoInt;
import daoint.UserDaoInt;

import acl.Rights;
import auth.MyAuthentication;

@ContextConfiguration(locations = { "classpath:testContext.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class UserTest {

	@Autowired
	private MyAuthentication authentication;
	
	@Autowired
	private AnnouncementInstanceDaoInt instanceDao;
	
	@Autowired
	private AnnouncementService service;
	
	@Autowired
	private AclDaoInt aclDao;
	
	@Autowired
	private UserDaoInt userDao;
	
	private User createUser(String username){
		User user = new User();
		user.setUsername(username);
		user.setGroups(new HashSet<Group>());
		userDao.save(user);
		return user;
	}
	
	private List<User> users;
	
	@Test
	public void testLogging() throws Exception{
		User thread1 = createUser("thread1");
		User main = createUser("mainthread");
		users = Arrays.asList(thread1, main);
		authentication.login("mainthread");
		aclDao.addAccess(Announcement.class, null, thread1, Rights.WRITE);
		Thread t1 = new LoginThread(thread1);
		t1.start();
		t1.join();
		assertEquals(1, instanceDao.getUnread().size());
		assertEquals("TTTT", instanceDao.getUnread().get(0).getAnnouncement().getTitle());
		assertEquals(main.getUsername(), instanceDao.getUnread().get(0).getReceiver().getUsername());
		instanceDao.read(instanceDao.getUnread().get(0));
		assertEquals("mainthread", MyAuthentication.getCurrentUser().getUsername());
	}
	
	private class LoginThread extends Thread{
		private User user;
		
		public LoginThread(User user){
			this.user = user;
		}
		public void run(){
			authentication.login(user.getUsername());
			assertEquals(user.getUsername(), MyAuthentication.getCurrentUser().getUsername());
			Announcement a = new Announcement();
			a.setTitle("TTTT");
			a.setBody("BBB");
			service.sendAnnouncement(a, users);
			assertEquals(1, instanceDao.getUnread().size());
			assertEquals("TTTT", instanceDao.getUnread().get(0).getAnnouncement().getTitle());
			assertEquals(user.getUsername(), instanceDao.getUnread().get(0).getReceiver().getUsername());
			instanceDao.read(instanceDao.getUnread().get(0));
		}
	}
}
