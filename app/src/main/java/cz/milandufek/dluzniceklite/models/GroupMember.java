package cz.milandufek.dluzniceklite.models;

public class GroupMember {

    private int id;
    private int groupId;
    private String name, email, contact, description;
    private int isMe;

    public GroupMember(int id, int groupId, String name, String email, String contact, String description, int isMe) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.description = description;
        this.isMe = isMe;
    }

    public GroupMember() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsMe() {
        return isMe;
    }

    public void setIsMe(int isMe) {
        this.isMe = isMe;
    }
}
