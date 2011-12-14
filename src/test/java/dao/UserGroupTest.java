package dao;

import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import daoint.GroupDaoInt;
import daoint.UserDaoInt;
import static org.junit.Assert.*;

import user.Group;
import user.User;


@ContextConfiguration(locations = { "classpath:testContext.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class UserGroupTest {

	@Autowired
	private UserDaoInt udao;
	
	@Autowired
	private GroupDaoInt gdao;

	/*
	 * 1) Create 2 groups
	 * 2) Add one user to 1st, one to 2nd, one to both
	 */
	
	
	@Test
	public void testCreatingGroups(){
		// 1
		Group g1 = new Group();
		g1.setName("group1");
		Group g2 = new Group();
		g2.setName("group2");
		Long g1id = gdao.save(g1);
		Long g2id = gdao.save(g2);
		//assertEquals(2, gdao.getAll().size());
		// 2
		User u1 = new User();
		u1.setUsername("u1");
		User u2 = new User();
		u2.setUsername("u2");
		User u3 = new User();
		u3.setUsername("u3");
		Long u1id = udao.save(u1);
		Long u2id = udao.save(u2);
		Long u3id = udao.save(u3);
		u1.setGroups(new HashSet<Group>());
		u2.setGroups(new HashSet<Group>());
		u3.setGroups(new HashSet<Group>());
		u1.getGroups().add(g1);
		u2.getGroups().add(g2);
		u3.getGroups().add(g1);
		u3.getGroups().add(g2);
		udao.saveOrUpdate(u1);
		udao.saveOrUpdate(u2);
		udao.saveOrUpdate(u3);
		assertEquals(2, gdao.get(g1id).getUsers().size());
		assertEquals(2, gdao.get(g2id).getUsers().size());
		assertEquals(1, udao.get(u1id).getGroups().size());
		assertEquals(1, udao.get(u2id).getGroups().size());
		assertEquals(2, udao.get(u3id).getGroups().size());
	}
}
