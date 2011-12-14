package acl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import aclint.AclInterface;

import user.User;

@Entity
@Table(name = "acl")
public class Acl implements AclInterface{
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private User user;

	private String classId; //class
	
	private Long objectId; //null if its class rights
	
	private Integer rights; //1 - RW; 0 -R

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getRights() {
		return rights;
	}

	public void setRights(Integer rights) {
		this.rights = rights;
	}
	
	public String toString(){
		return "ACL(" + user + " -> " + objectId + ", " + rights + ")";
	}
}
