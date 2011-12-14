package acl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import user.Group;

@Entity
@Table(name = "groupacl")
public class GroupAcl implements AclInterface {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private Group group;

	private String classId; //class
	
	private Long objectId; //null if its class rights
	
	private Integer rights; //0 R; 1 RW

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Integer getRights() {
		return rights;
	}

	public void setRights(Integer rights) {
		this.rights = rights;
	}
	
	
}
