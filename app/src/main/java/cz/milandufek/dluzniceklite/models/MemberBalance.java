package cz.milandufek.dluzniceklite.models;

import java.util.Comparator;

public class MemberBalance {

    private int groupId, memberId;
    private String memberName;
    private double balance;

    public MemberBalance(int groupId, int memberId, String memberName, double balance) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.balance = balance;
    }

    public static Comparator<MemberBalance> SortByBalance = (o1, o2) -> Double.compare(o1.getBalance(), o2.getBalance());

    public int getGroupId() {
        return groupId;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return MemberBalance.class.toString() + " = { " +
                "gID = " + groupId +
                ",mID = " + memberId +
                ",name = " + memberName +
                ",balance = " + balance +
                " }";
    }
}
