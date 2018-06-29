package cz.milandufek.dluzniceklite.models;

public class Transaction {
    private static final String TAG = "Transaction";

    private double amount;
    private int id, debtor_id, expense_id;

    public Transaction() {
    }

    public Transaction(int id, int debtor_id, int expense_id, double amount) {
        this.id = id;
        this.amount = amount;
        this.debtor_id = debtor_id;
        this.expense_id = expense_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDebtor_id() {
        return debtor_id;
    }

    public void setDebtor_id(int debtor_id) {
        this.debtor_id = debtor_id;
    }

    public int getExpense_id() {
        return expense_id;
    }

    public void setExpense_id(int expense_id) {
        this.expense_id = expense_id;
    }
}
