package cz.milandufek.dluzniceklite.models;

public final class GroupMember {

    private int id;
    private int groupId;
    private String name, email, contact, description;
    private boolean activePayments;

    public GroupMember(int id, int groupId, String name, String email, String contact,
                       String description, boolean activePayments) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.description = description;
        this.activePayments = activePayments;
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getDescription() {
        return description;
    }

    public boolean getActivePayments() {
        return activePayments;
    }

    @Override
    public String toString() {
        return GroupMember.class.toString() + " = { " +
                "id = " + id +
                ", groupId = " + groupId +
                ", name = " + name +
                ", email = " + email +
                ", contact = " + contact +
                ", description = " + description +
                ", activePayments = " + activePayments +
                " }";
    }
}
