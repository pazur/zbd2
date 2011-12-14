package daoint;

import user.Group;
import user.User;

public interface AclDaoInt {
	public int checkAcl(Class<?> cls, Long id);
	public void addAccess(Class<?> cls, Long id, Group g, int rights);
	public void addAccess(Class<?> cls, Long id, User g, int rights);
	public void revokeAccess(Class<?> cls, Long id, Group g);
	public void revokeAccess(Class<?> cls, Long id, User g);
	public void removeAclsFor(Class<?> cls, Long id);
}
