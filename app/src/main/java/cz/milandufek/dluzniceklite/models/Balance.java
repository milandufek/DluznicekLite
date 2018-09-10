package cz.milandufek.dluzniceklite.models;

import java.util.Comparator;

public class Balance {

    private int groupId, memberId, currencyId;
    private String memberName;
    private double balance;

    public Balance() {
    }

    public static Comparator<Balance> SortByBalance = new Comparator<Balance>() {
        @Override
        public int compare(Balance o1, Balance o2) {
            return Double.compare(o1.getBalance(), o2.getBalance());
        }
    };

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

    @Override
    public String toString() {
        return memberName;
//        return "[ gID = " + groupId + " ] " +
//                "[ mID = " + memberId + " ] " +
//                "[ cId = " + currencyId + " ] " +
//                "[ name = " + memberName + " ] " +
//                "[ balance = " + balance + " ] ";
    }
}
