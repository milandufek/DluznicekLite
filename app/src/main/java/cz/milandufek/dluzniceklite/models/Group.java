package cz.milandufek.dluzniceklite.models;

/**
 *  Class for object - GROUP
 */
public class Group {
    private static final String TAG = "Group";

    private int id;
    private String name, description;
    private int currency;

    public Group() {
    }

    public Group(int id, String name, int currency, String description) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.description = description;
    }

    /**
     * Getters & Setters
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Group {" +
                "id=" + id + "\'" +
                ", name='" + name + "\'" +
                ", currency'" + currency + "\'" +
                '}';
    }
}
