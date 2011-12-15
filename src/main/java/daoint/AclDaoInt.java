package daoint;

import acl.Rights;
import user.Group;
import user.User;

public interface AclDaoInt {
	public Rights checkAcl(Class<?> cls, Long id);
	public void addAccess(Class<?> cls, Long id, Group g, Rights rights);
	public void addAccess(Class<?> cls, Long id, User g, Rights rights);
	public void revokeAccess(Class<?> cls, Long id, Group g);
	public void revokeAccess(Class<?> cls, Long id, User g);
	public void removeAclsFor(Class<?> cls, Long id);
}
