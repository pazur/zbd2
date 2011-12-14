package content;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("announcement")
public class Announcement extends Content {

}
