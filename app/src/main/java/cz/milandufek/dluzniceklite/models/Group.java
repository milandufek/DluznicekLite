package cz.milandufek.dluzniceklite.models;

/**
 *  Class for object - GROUP
 */
public class Group {

    private int id;
    private String name, description;
    private int currency;

    public Group(int id, String name, int currency, String description) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }
}
