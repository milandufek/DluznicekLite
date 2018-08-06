package cz.milandufek.dluzniceklite.models;

import android.support.annotation.NonNull;

public class Balance implements Comparable<Balance> {

    private int groupId, memberId, currencyId;
    private String memberName;
    private double balance;

    public Balance() {
    }

    @Override
    public int compareTo(@NonNull Balance o) {
        int balance = (int) this.balance * 100;
        int balanceToCompare = (int) o.balance * 100;

        return (balance - balanceToCompare);
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
