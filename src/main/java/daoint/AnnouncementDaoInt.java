package daoint;

import java.util.List;

import content.Announcement;

public interface AnnouncementDaoInt {
	public Announcement get(Long id);
	public Long save(Announcement announcement);
	public void saveOrUpdate(Announcement announcement);
	public List<Announcement> getAll();
}
