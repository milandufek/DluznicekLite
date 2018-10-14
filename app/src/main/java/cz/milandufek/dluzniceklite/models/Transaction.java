package cz.milandufek.dluzniceklite.models;

public final class Transaction {

    private double amount;
    private int id, debtor_id, expense_id;
    private boolean isSettleUpTransaction;

    public Transaction(int id, int debtor_id, int expense_id, double amount) {
        this.id = id;
        this.amount = amount;
        this.debtor_id = debtor_id;
        this.expense_id = expense_id;
    }

    public Transaction(int id, int debtor_id, int expense_id, double amount, boolean isSettleUpTransaction) {
        this.id = id;
        this.amount = amount;
        this.debtor_id = debtor_id;
        this.expense_id = expense_id;
        this.isSettleUpTransaction = isSettleUpTransaction;
    }


    public double getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public int getDebtor_id() {
        return debtor_id;
    }

    public int getExpense_id() {
        return expense_id;
    }

    public boolean getIsSettleUpTransaction() {
        return isSettleUpTransaction;
    }

    @Override
    public String toString() {
        return Transaction.class.toString() + " = { " +
                "id = " + id +
                ", debtor_id = " + debtor_id +
                ", expense_id = " + expense_id +
                ", amount = " + amount +
                " }";
    }
}
