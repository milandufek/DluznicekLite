package cz.milandufek.dluzniceklite.models;

import java.util.Comparator;

public class MemberBalance {

    private int groupId, memberId, currencyId;
    private String memberName;
    private double balance;

    public MemberBalance() {
    }

    public static Comparator<MemberBalance> SortByBalance = (o1, o2) -> Double.compare(o1.getBalance(), o2.getBalance());

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
        return "[ gID = " + groupId + " ] " +
                "[ mID = " + memberId + " ] " +
                "[ cId = " + currencyId + " ] " +
                "[ name = " + memberName + " ] " +
                "[ balance = " + balance + " ] ";
    }
}
