package acl;

import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import content.Announcement;
import content.Content;
import static org.junit.Assert.*;

import auth.MyAuthentication;

import user.User;
import user.Group;

import daoint.AclDaoInt;
import daoint.AnnouncementDaoInt;
import daoint.GroupDaoInt;
import daoint.UserDaoInt;


@ContextConfiguration(locations = { "classpath:testContext.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class AclTest {
	
	@Autowired
	private AclDaoInt dao;

	@Autowired
	private UserDaoInt udao;
	
	@Autowired
	private GroupDaoInt gdao;

	@Autowired
	private MyAuthentication authentication;
	
	@Autowired
	private AnnouncementDaoInt adao;
	
	@Test
	public void testAuthentication(){
		User u = new User();
		u.setUsername("acltest00");
		udao.save(u);
		u.setUsername("acltest01");
		udao.save(u);
		authentication.login("acltest00");
		assertEquals("acltest00", MyAuthentication.getCurrentUser().getUsername());
		authentication.login("acltest01");
		assertEquals("acltest01", MyAuthentication.getCurrentUser().getUsername());
	}
	
	@Test
	public void testUserToClassAcl(){
		User u = new User();
		u.setUsername("acltest1");
		udao.save(u);
		authentication.login("acltest1");
		assertEquals(Rights.NONE, dao.checkAcl(Content.class, null));
		dao.addAccess(Content.class, null, u, Rights.READ);
		assertEquals(Rights.READ, dao.checkAcl(Content.class, null));
		dao.addAccess(Content.class, null, u, Rights.WRITE);
		assertEquals(Rights.WRITE, dao.checkAcl(Content.class, null));
		dao.addAccess(Content.class, null, u, Rights.READ);
		assertEquals(Rights.WRITE, dao.checkAcl(Content.class, null));
	}
	
	@Test
	public void testUserToObjectAcl(){
		User u = new User();
		u.setUsername("acltest2");
		udao.save(u);
		authentication.login("acltest2");
		assertEquals(Rights.NONE, dao.checkAcl(Content.class, 1L));
		dao.addAccess(Content.class, 1L, u, Rights.READ);
		assertEquals(Rights.READ, dao.checkAcl(Content.class, 1L));
		dao.addAccess(Content.class, null, u, Rights.WRITE);
		assertEquals(Rights.WRITE, dao.checkAcl(Content.class, 1L));
		dao.addAccess(Content.class, 1L, u, Rights.READ);
		assertEquals(Rights.WRITE, dao.checkAcl(Content.class, 1L));
	}
	
	@Test
	public void testRevokeUserToClassAcl(){
		User u = new User();
		u.setUsername("acltest3");
		udao.save(u);
		authentication.login("acltest3");
		assertEquals(Rights.NONE, dao.checkAcl(Content.class, null));
		dao.addAccess(Content.class, null, u, Rights.READ);
		assertEquals(Rights.READ, dao.checkAcl(Content.class, null));
		dao.addAccess(Content.class, null, u, Rights.WRITE);
		assertEquals(Rights.WRITE, dao.checkAcl(Content.class, null));
		dao.revokeAccess(Content.class, null, u);
		assertEquals(Rights.NONE, dao.checkAcl(Content.class, null));
	}
	
	@Test
	public void testRevokeUserToObjectAcl(){
		User u = new User();
		u.setUsername("acltest4");
		udao.save(u);
		authentication.login("acltest4");
		assertEquals(Rights.NONE, dao.checkAcl(Content.class, 1L));
		dao.addAccess(Content.class, null, u, Rights.READ);
		assertEquals(Rights.READ, dao.checkAcl(Content.class, null));
		assertEquals(Rights.READ, dao.checkAcl(Content.class, 1L));
		dao.addAccess(Content.class, 1L, u, Rights.WRITE);
		assertEquals(Rights.WRITE, dao.checkAcl(Content.class, 1L));
		dao.revokeAccess(Content.class, 1L, u);
		assertEquals(Rights.READ, dao.checkAcl(Content.class, 1L));
	}
	
	@Test
	public void testUserToClassInheritance(){
		User u = new User();
		u.setUsername("acltest5");
		udao.save(u);
		authentication.login("acltest5");
		assertEquals(Rights.NONE, dao.checkAcl(Content.class, null));
		assertEquals(Rights.NONE, dao.checkAcl(Announcement.class, null));
		dao.addAccess(Content.class, null, u, Rights.READ);
		assertEquals(Rights.READ, dao.checkAcl(Content.class, null));
		assertEquals(Rights.READ, dao.checkAcl(Announcement.class, null));
		dao.addAccess(Content.class, null, u, Rights.WRITE);
		assertEquals(Rights.WRITE, dao.checkAcl(Content.class, null));
		assertEquals(Rights.WRITE, dao.checkAcl(Announcement.class, null));
		dao.addAccess(Content.class, null, u, Rights.READ);
		assertEquals(Rights.WRITE, dao.checkAcl(Content.class, null));
		assertEquals(Rights.WRITE, dao.checkAcl(Announcement.class, null));
	}
	
	private void announcementAccessDenied(Long id){
		try{
			adao.get(id);
			fail();
		} catch (HibernateSystemException c){
		}
	}
	
	@Test
	public void testLoad(){
		User u = new User();
		u.setUsername("acltest6");
		udao.save(u);
		authentication.login("acltest6");
		Announcement a = new Announcement();
		a.setBody("ble");
		a.setTitle("bleble");
		dao.addAccess(Announcement.class, null, u, Rights.WRITE);
		Long id = adao.save(a);
		dao.revokeAccess(Announcement.class, null, u);
		announcementAccessDenied(id);
		//Add read to class
		dao.addAccess(Announcement.class, null, u, Rights.READ);
		adao.get(id);
		//Revoke read to class
		dao.revokeAccess(Announcement.class, null, u);
		announcementAccessDenied(id);
		//Add read to instance
		dao.addAccess(Announcement.class, id, u, Rights.READ);
		adao.get(id);
		//Revoke read to instance
		dao.revokeAccess(Announcement.class, id, u);
		announcementAccessDenied(id);
		//Add read to superclass
		dao.addAccess(Content.class, null, u, Rights.READ);
		adao.get(id);
		//Revoke read to superclass
		dao.revokeAccess(Content.class, null, u);
		announcementAccessDenied(id);
		dao.addAccess(Content.class, id, u, Rights.READ);
		announcementAccessDenied(id);
	}
	
	@Test
	public void testGroups(){
		User u = new User();
		u.setUsername("acltest7");
		u.setGroups(new HashSet<Group>());
		udao.save(u);
		authentication.login("acltest7");
		Group g1 = new Group(), g2 = new Group();
		g1.setName("g1");
		gdao.save(g1);
		g2.setName("g2");
		gdao.save(g2);
		dao.addAccess(Announcement.class, null, g1, Rights.READ);
		dao.addAccess(Announcement.class, null, g2, Rights.WRITE);
		assertEquals(Rights.NONE, dao.checkAcl(Announcement.class, null));
		u.getGroups().add(g1);
		udao.saveOrUpdate(u);
		assertEquals(Rights.READ, dao.checkAcl(Announcement.class, null));
		u.getGroups().add(g2);
		udao.saveOrUpdate(u);
		assertEquals(Rights.WRITE, dao.checkAcl(Announcement.class, null));
	}
}
