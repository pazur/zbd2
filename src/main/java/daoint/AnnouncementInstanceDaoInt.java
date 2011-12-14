package daoint;

import java.util.Collection;
import java.util.List;

import content.AnnouncementInstance;

public interface AnnouncementInstanceDaoInt {

	public void saveAll(Collection<AnnouncementInstance> ais);
	public void update(AnnouncementInstance ai);
	public void delete(AnnouncementInstance ai);
	public List<AnnouncementInstance> getUnread();
	public void save(AnnouncementInstance ai);
	public void read(AnnouncementInstance ai);
}
