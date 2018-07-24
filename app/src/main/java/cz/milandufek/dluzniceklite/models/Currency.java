package cz.milandufek.dluzniceklite.models;

/**
 *  Class for object - currency
 */
public class Currency {
    private static final String TAG = "Currency";

    private int id, quantity, baseCurrency, isBaseCurrency, isDeletable;
    private String name, country;
    private double exchangeRate;

    public Currency() {
    }

    public Currency(int id, String name, String country, int quantity, double exchangeRate,
                    int baseCurrency, int isBaseCurrency, int isDeletable) {

        this.id = id;
        this.country = country;
        this.name = name;
        this.quantity = quantity;
        this.exchangeRate = exchangeRate;
        this.baseCurrency = baseCurrency;
        this.isBaseCurrency = isBaseCurrency;
        this.isDeletable = isDeletable;
    }

    /**
     *  Getters & Setters
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(int baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public int getIsBaseCurrency() {
        return isBaseCurrency;
    }

    public void setIsBaseCurrency(int isBaseCurrency) {
        this.isBaseCurrency = isBaseCurrency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public int getIsDeletable() {
        return isDeletable;
    }

    public void setIsDeletable(int isDeletable) {
        this.isDeletable = isDeletable;
    }

    public String getCurrencyInfo() {
        StringBuilder info = new StringBuilder(quantity);
        info.append(" : ");
        info.append(exchangeRate);
        info.append(" \u2022 ");
        if (! this.country.equals("")) {
            info.append(" (");
            info.append(this.country);
            info.append(" )");
        }
        return info.toString();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
