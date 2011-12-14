package user;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "users")
public class User{

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private String username;

	@ManyToMany(targetEntity=user.Group.class, fetch = FetchType.EAGER)
	private Set<Group> groups;// = new HashSet<Group>();
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString(){
		return username;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User){
			if (id != null && ((User)obj).id != null){
				return id.equals(((User)obj).id);
			}
			return false;
		}
		return super.equals(obj);
	}
	
	
}
