package daoint;

import java.util.List;

import user.Group;

public interface GroupDaoInt {
	public Long save(Group group);
	public Group get(String name);
	public Group get(Long id);
	public List<Group> getAll();
	public void saveOrUpdate(Group group);
	public void deleteAll();
}
