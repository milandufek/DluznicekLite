package cz.milandufek.dluzniceklite.models;

/**
 *  Class for object - currency
 */
public class Currency {

    private final int id, quantity, baseCurrency;
    private final String name, country;
    private final double exchangeRate;
    private final boolean isBaseCurrency, isDeletable;

    // TODO think about using builder pattern and Java.util.Currency
    public Currency(int id, String name, String country, int quantity, double exchangeRate,
                    int baseCurrency, boolean isBaseCurrency, boolean isDeletable) {

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

    public int getQuantity() {
        return quantity;
    }

    public int getBaseCurrency() {
        return baseCurrency;
    }

    public String getName() {
        return name;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public boolean getIsBaseCurrency() {
        return isBaseCurrency;
    }

    public boolean getIsDeletable() {
        return isDeletable;
    }

    public String getCurrencyInfo() {
        StringBuilder info = new StringBuilder();
        info.append(quantity);
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
}
