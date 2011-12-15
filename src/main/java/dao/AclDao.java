package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import daoint.AclDaoInt;
import daoint.UserDaoInt;

import acl.Acl;
import acl.GroupAcl;
import acl.Rights;
import auth.MyAuthentication;

import user.Group;
import user.User;

@Repository
@Transactional(readOnly = true)
public class AclDao extends HibernateDaoSupport implements AclDaoInt{
	
	@Autowired
	private UserDaoInt udao;
	
	public Rights checkAcl(Class<?> cls, Long id){
		User currentUser = MyAuthentication.getCurrentUser();
		currentUser = udao.get(currentUser.getId());
		int a1 = getAcls(cls, id, currentUser, true);
		int a2 = getAcls(cls, id, currentUser, false);
		return fromInt(Math.max(a1,a2 ));
	}

	private String getAclDeleteQuery(){
		return "from Acl where classId = (:cls) and objectId = (:oid)";
	}

	private String getGroupAclDeleteQuery(){
		return "from GroupAcl where classId = (:cls) and objectId = (:oid)";
	}
	
	private String getAclQueryObject(){
		return "from Acl where classId = (:cls) and user = (:uorg) and objectId = (:oid)";
	}
	private String getAclQueryClass(){
		return "from Acl where classId = (:cls) and user = (:uorg) and objectId is null";
	}
	private String getAclQueryInheritance(){
		return "from Acl where classId in (:cls) and user = (:uorg) and objectId is null";
	}
	
	private String getGroupAclQueryObject(){
		return "from GroupAcl where classId = (:cls) and group = (:uorg) and objectId = (:oid)";
	}
	
	private String getGroupAclQueryClass(){
		return "from GroupAcl where classId = (:cls) and group = (:uorg) and objectId is null";
	}	
	
	private String getGroupAclQueryGroupsObject(){
		return "from GroupAcl where classId = (:cls) and group in (:uorg) and objectId = (:oid)";
	}
	
	private String getGroupAclQueryGroupsInheritance(){
		return "from GroupAcl where classId in (:cls) and group in (:uorg) and objectId is null";
	}
	
	@SuppressWarnings("rawtypes")
	private List queryObject(Class<?> cls, Long id, User user, boolean group, String prefix){
		String classId = cls.getName();
		String findQuery;
		Object groupOrUser;
		if(group){
			findQuery = getGroupAclQueryGroupsObject();
			Set<Group> groups = user.getGroups();
			groupOrUser = groups;
			if (groups.isEmpty()){
				List<Integer> result =  new ArrayList<Integer>();
				result.add(null);
				return result;
			}
			groupOrUser = groups;
		}else{
			findQuery = getAclQueryObject();
			groupOrUser = user;
		}
		return getHibernateTemplate().findByNamedParam(prefix + findQuery, new String[] {"cls", "uorg", "oid"}, new Object[] {classId, groupOrUser, id});
	}
	
	@SuppressWarnings("rawtypes")
	private List queryClasses(Class<?> cls, User user, boolean group, String prefix){
		List<String> classIds = getAllClasses(cls);
		String findQuery;
		Object groupOrUser;
		if(group){
			findQuery = getGroupAclQueryGroupsInheritance();
			Set<Group> groups = user.getGroups();
			if (groups.isEmpty()){
				List<Integer> result =  new ArrayList<Integer>();
				result.add(null);
				return result;
			}
			groupOrUser = groups;
		}else{
			findQuery = getAclQueryInheritance();
			groupOrUser = user;
		}
		return getHibernateTemplate().findByNamedParam(prefix + findQuery, new String[] {"cls", "uorg"}, new Object[] {classIds, groupOrUser});
	}
	
	/*
	 * returns Acl or GroupAcl where classId, id, and group/user are equal to arguments
	 */
	@SuppressWarnings("rawtypes")
	private List queryExact(Class<?> cls, Long id, Object userOrGroup, boolean group){
		String findQuery;
		if(group){
			if(id == null)
				findQuery = getGroupAclQueryClass();
			else
				findQuery = getGroupAclQueryObject();
		}else{
			if(id == null)
				findQuery = getAclQueryClass();
			else
				findQuery = getAclQueryObject();
		}
		if (id == null)
			return getHibernateTemplate().findByNamedParam(findQuery, new String[] {"cls", "uorg"}, new Object[] {cls.getName(), userOrGroup});
		return getHibernateTemplate().findByNamedParam(findQuery, new String[] {"cls", "uorg", "oid"}, new Object[] {cls.getName(), userOrGroup, id});
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addAccess(Class<?> cls, Long id, Group g, Rights r){
		int rights = toInt(r);
		@SuppressWarnings("unchecked")
		List<GroupAcl> acls = queryExact(cls, id, g, true);
		if(acls.isEmpty()){
			GroupAcl acl = new GroupAcl();
			acl.setClassId(cls.getName());
			acl.setObjectId(id);
			acl.setGroup(g);
			acl.setRights(rights);
			getHibernateTemplate().save(acl);
		}else{
			GroupAcl acl = acls.get(0);
			acl.setRights(Math.max(rights, acl.getRights()));
			getHibernateTemplate().update(acl);
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addAccess(Class<?> cls, Long id, User g, Rights r){
		int rights = toInt(r);
		@SuppressWarnings("unchecked")
		List<Acl> acls = queryExact(cls, id, g, false);
		if(acls.isEmpty()){
			Acl acl = new Acl();
			acl.setClassId(cls.getName());
			acl.setObjectId(id);
			acl.setUser(g);
			acl.setRights(rights);
			getHibernateTemplate().save(acl);
		}else{
			Acl acl = acls.get(0);
			acl.setRights(Math.max(rights, acl.getRights()));
			getHibernateTemplate().update(acl);
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeAclsFor(Class<?> cls, Long id){
		getHibernateTemplate().deleteAll(
			getHibernateTemplate().findByNamedParam(getAclDeleteQuery(),
				new String[] {"cls", "oid"}, new Object[] {cls.getName(), id}
			)
		);
		getHibernateTemplate().deleteAll(
			getHibernateTemplate().findByNamedParam(getGroupAclDeleteQuery(), 
				new String[] {"cls", "oid"}, new Object[] {cls.getName(), id}
			)
		);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void revokeAccess(Class<?> cls, Long id, Group g){
		getHibernateTemplate().deleteAll(queryExact(cls, id, g, true));
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void revokeAccess(Class<?> cls, Long id, User g){
		getHibernateTemplate().deleteAll(queryExact(cls, id, g, false));
	}

	/*
	 * class id <- object whose permissions we want
	 * group <- if true we want group acls
	 */
	private int getAcls(Class<?> cls, Long id, User currentUser, boolean group){
		Integer classResult = -1;
		Integer objectResult = -1;
		Integer rawClassResult = (Integer) queryClasses(cls, currentUser, group, "select max(rights) ").get(0);
		if (rawClassResult != null){
			classResult = rawClassResult;
		}
		if (id != null){
			Integer rawObjectResult = (Integer) queryObject(cls, id, currentUser, group, "select max(rights) ").get(0);
			if (rawObjectResult != null){
				objectResult = rawObjectResult;
			}
		}
		return Math.max(classResult, objectResult);
	}

	private List<String> getAllClasses(Class<?> cls){
		List<String> result = new ArrayList<String>();
		while(cls != null){
			result.add(cls.getName());
			cls = cls.getSuperclass();
		}
		return result;
	}
	
	private int toInt(Rights rights){
		if (rights == Rights.NONE)
			throw new RuntimeException("Cannot add NONE rights");
		if (rights == Rights.READ)
			return 0;
		return 1;
	}
	private Rights fromInt(int rights){
		if (rights == 0)
			return Rights.READ;
		if (rights == -1)
			return Rights.NONE;
		return Rights.WRITE;
	}
}
