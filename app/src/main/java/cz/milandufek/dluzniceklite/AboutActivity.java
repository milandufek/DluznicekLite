package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = "AboutActivity";

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // reset button
        /*
        Button mBtnReset = findViewById(R.id.btn_settings_resetdata);
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Resetting all data!");

                SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
                try {
                    db.beginTransaction();
                    db.delete(CurrencySql.TABLE_NAME, null, null);
                    db.delete(TransactionSql.TABLE_NAME, null, null);
                    db.delete(ExpenseSql.TABLE_NAME, null, null);
                    db.delete(GroupMemberSql.TABLE_NAME, null, null);
                    db.delete(GroupSql.TABLE_NAME, null, null);
                    db.delete("sqlite_sequence", null, null);
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }

                // insert currencies
                List<Currency> currencies = new ArrayList<>();
                currencies.add(new Currency(0,"CZK","Česká Republika",
                        1,1,1,1,0));
                currencies.add(new Currency(1,"USD","USA",
                        1,20,1,0,1));
                currencies.add(new Currency(2,"EUR","EMU",
                        1,25,1,0,1));
                currencies.add(new Currency(3,"DNG","Vietnam",
                        1000,0.97,1,0,1));
                currencies.add(new Currency(4,"LKR","Srí Lanka",
                        7,7,1,0,1));
                currencies.add(new Currency(5,"GPB","Velká Británie",
                        1,29.5,1,0,1));
                currencies.add(new Currency(6,"ISK","Island",
                        100,20.5,1,0,1));
                currencies.add(new Currency(7,"BTC","Nikde",
                        1,100000,1,0,1));
                CurrencySql currencyRepo = new CurrencySql();
                for (int i = 0; currencies.size() > i; i++) {
                    currencyRepo.insertCurrency(currencies.get(i));
                }

                // insert groups & members
                List<GroupMember> groupMembers = new ArrayList<>();
                GroupSql groupRepo = new GroupSql();
                GroupMemberSql groupMemberRepo = new GroupMemberSql();
                long gId;

                Group group = new Group(1, "Běžci", 1,"Prostě běžci...");
                gId = groupRepo.insertGroup(group);
                groupMembers.clear();
                groupMembers.add(new GroupMember(0, (int)gId, "Milan", null, null, "Ironman", 1));
                groupMembers.add(new GroupMember(0, (int)gId, "Kačka", null, null, "Ultra", 0));
                groupMembers.add(new GroupMember(0, (int)gId, "Viktor", null, null, "Neběžec", 0));
                groupMembers.add(new GroupMember(0, (int)gId, "Jana", null, null, "RUM", 0));
                for (int i = 0; groupMembers.size() > i; i++) {
                    groupMemberRepo.insertGroupMember(groupMembers.get(i));
                }

                group = new Group(2, "Sri Lanka", 5,"Pohoda");
                gId = groupRepo.insertGroup(group);
                groupMembers.clear();
                groupMembers.add(new GroupMember(1, (int)gId, "Milan", "", "", "", 1));
                groupMembers.add(new GroupMember(0, (int)gId, "Jarka", "", "", "", 0));
                for (int i = 0; groupMembers.size() > i; i++) {
                    groupMemberRepo.insertGroupMember(groupMembers.get(i));
                }

                group = new Group(3, "Vietnam", 4,"Xia Thao Pho Bo Camon");
                gId = groupRepo.insertGroup(group);
                groupMembers.clear();
                groupMembers.add(new GroupMember(2, (int)gId, "Milan", "", "", "", 1));
                groupMembers.add(new GroupMember(2, (int)gId, "Viktor", "", "", "", 10));
                for (int i = 0; groupMembers.size() > i; i++) {
                    groupMemberRepo.insertGroupMember(groupMembers.get(i));
                }

                group = new Group(4, "Road trip in UK", 6,"IPA forever");
                gId = groupRepo.insertGroup(group);
                groupMembers.clear();
                groupMembers.add(new GroupMember(3, (int)gId, "Ohin", "", "", "", 1));
                groupMembers.add(new GroupMember(3, (int)gId, "Milan", "", "", "", 0));
                for (int i = 0; groupMembers.size() > i; i++) {
                    groupMemberRepo.insertGroupMember(groupMembers.get(i));
                }

                group = new Group(5, "Island crew", 7,"Road trip na Islandu");
                gId = groupRepo.insertGroup(group);
                groupMembers.clear();
                groupMembers.add(new GroupMember(0, (int)gId, "Milan", "", "", "", 1));
                groupMembers.add(new GroupMember(0, (int)gId, "Martin", "", "", "", 0));
                groupMembers.add(new GroupMember(0, (int)gId, "Filip", "", "", "", 0));
                groupMembers.add(new GroupMember(0, (int)gId, "Lenka", "", "", "", 0));
                groupMembers.add(new GroupMember(0, (int)gId, "Mája", "", "", "", 0));
                for (int i = 0; groupMembers.size() > i; i++) {
                    groupMemberRepo.insertGroupMember(groupMembers.get(i));
                }


                // expenses & transactions
                ExpenseSql expenseRepo = new ExpenseSql();

                List<Transaction> transactions = new ArrayList<>();
                TransactionSql transactionRepo = new TransactionSql();
                Expense expense;
                long expensesId;

                // prvni skupina Runners
                expense = new Expense(0, 1,1,1,"Večeře", "2018-01-16", "21:00");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 1, (int)expensesId, 270));
                transactions.add(new Transaction(0, 2, (int)expensesId, 270));
                transactions.add(new Transaction(0, 3, (int)expensesId, 270));
                transactions.add(new Transaction(0, 4, (int)expensesId, 270));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 1,1,3,"Boty", "2018-06-12", "13:37");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 2, (int)expensesId, 110));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 3,1,1,"Piva", "2018-06-16", "23:46");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 1, (int)expensesId, 200));
                transactions.add(new Transaction(0, 2, (int)expensesId, 100));
                transactions.add(new Transaction(0, 3, (int)expensesId, 100));
                transactions.add(new Transaction(0, 4, (int)expensesId, 250));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 2,1,1,"Útrata u Dřeváka...", "2018-06-26", "22:12");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 1, (int)expensesId, 435));
                transactions.add(new Transaction(0, 2, (int)expensesId, 435));
                transactions.add(new Transaction(0, 3, (int)expensesId, 435));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 1,1,2,"Závod", "2018-06-10", "23:26");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 1, (int)expensesId, 11));
                transactions.add(new Transaction(0, 2, (int)expensesId, 11));
                transactions.add(new Transaction(0, 4, (int)expensesId, 11));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 3,1,1,"Snídaně", "2018-06-06", "08:20");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 1, (int)expensesId, 90));
                transactions.add(new Transaction(0, 2, (int)expensesId, 90));
                transactions.add(new Transaction(0, 3, (int)expensesId, 90));
                transactions.add(new Transaction(0, 4, (int)expensesId, 90));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 2,1,1,"Piva", "2018-06-16", "23:46");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 2, (int)expensesId, 100));
                transactions.add(new Transaction(0, 3, (int)expensesId, 100));
                transactions.add(new Transaction(0, 4, (int)expensesId, 100));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 4,1,1,"Za cestu", "2018-06-16", "11:11");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 1, (int)expensesId, 267));
                transactions.add(new Transaction(0, 4, (int)expensesId, 267));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                // druha skupina Vietnam
                transactions.clear();
                expense = new Expense(0, 7,3,4,"Bia Hoi", "2017-01-10", "18:00");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 7, (int)expensesId, 24000));
                transactions.add(new Transaction(0, 8, (int)expensesId, 24000));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 7,3,4,"Bia Hoi", "2017-01-12", "22:17");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 7, (int)expensesId, 7000));
                transactions.add(new Transaction(0, 8, (int)expensesId, 24000));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 8,3,4,"Pho Bo", "2017-01-13", "13:33");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 7, (int)expensesId, 60000));
                transactions.add(new Transaction(0, 8, (int)expensesId, 60000));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 7,3,4,"Bun Bo Nam Bo", "2017-01-16", "18:56");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 7, (int)expensesId, 90000));
                transactions.add(new Transaction(0, 8, (int)expensesId, 90000));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

                transactions.clear();
                expense = new Expense(0, 7,3,4,"Bankomat", "2017-01-04", "12:44");
                expensesId = expenseRepo.insertExpense(expense);
                transactions.add(new Transaction(0, 8, (int)expensesId, 250000));
                for (int i = 0; transactions.size() > i; i++) {
                    transactionRepo.insertTransaction(transactions.get(i));
                }

            }
        });
        */
    }
}
