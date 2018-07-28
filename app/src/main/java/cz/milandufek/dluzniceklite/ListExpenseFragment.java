package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.ExpenseItem;
import cz.milandufek.dluzniceklite.models.ExpenseSummary;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;
import cz.milandufek.dluzniceklite.utils.ExpenseRVAdapter;
import cz.milandufek.dluzniceklite.utils.MyNumbers;
import cz.milandufek.dluzniceklite.utils.MySharedPreferences;

public class ListExpenseFragment extends Fragment {
    private static final String TAG = "ListExpenseFragment";

    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_main_expenses, container, false);

        context = getActivity();

        TextView summaryTitle = view.findViewById(R.id.tv_expense_summary_title);
        ExpenseSummary summaryValues = initDataSummary();
        String titleText = getString(R.string.total_spent);
        summaryTitle.setText(titleText);

        TextView summarySum = view.findViewById(R.id.tv_expense_summary_amount);
        StringBuilder sumSpent = new StringBuilder();
            sumSpent.append(summaryValues.getSumSpent());
            sumSpent.append(" ");
            sumSpent.append(summaryValues.getCurrencyName());
        summarySum.setText(sumSpent);

        RecyclerView recyclerView = view.findViewById(R.id.rv_expense_list);
        ExpenseRVAdapter adapter = new ExpenseRVAdapter(context, recViewDataSet());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    /**
     *  Load data from database and prepare them to display
     */
    private List<ExpenseItem> recViewDataSet() {
        Log.d(TAG, "recViewDataSet: for Rec View");

        List<ExpenseItem> expenseItems = new ArrayList<>();
        int groupId = new MySharedPreferences(context).getActiveGroupId();
        Cursor cursorExpenseItems = new ExpenseRepo().selectExpenseItems(groupId);
        while (cursorExpenseItems.moveToNext()) {
            ExpenseItem expenseItem = new ExpenseItem();
            expenseItem.setId(cursorExpenseItems.getInt(0));
            expenseItem.setPayer(cursorExpenseItems.getString(1));
            expenseItem.setCurrency(cursorExpenseItems.getString(2));
            expenseItem.setReason(cursorExpenseItems.getString(3));
            expenseItem.setDate(cursorExpenseItems.getString(4));
            expenseItem.setTime(cursorExpenseItems.getString(5));

            // select all transactions related to the expense
            double sum = 0;
            Cursor selectTransactions = new TransactionRepo()
                    .selectTransactionForExpense(expenseItem.getId());
            StringBuilder debtors = new StringBuilder();
            String separator = "";
            while (selectTransactions.moveToNext()) {
                debtors.append(separator);
                separator = ", ";
                debtors.append(selectTransactions.getString(0));
                sum += selectTransactions.getDouble(1);
            }
            selectTransactions.close();

            sum = new MyNumbers().roundIt(sum, 2);
            expenseItem.setDebtors(debtors.toString());
            expenseItem.setAmount(sum);

            expenseItems.add(expenseItem);
        }
        cursorExpenseItems.close();

        return expenseItems;
    }

    /**
     * Select currency name and total amount spent, converted to active group base currency
     * @return ExpenseSummary [ currencyName, totalSpent ]
     */
    private ExpenseSummary initDataSummary() {
        MySharedPreferences sp = new MySharedPreferences(context);
        int groupId = sp.getActiveGroupId();
        int currencyId = sp.getActiveGroupCurrency();

        String currencyName = new CurrencyRepo().getCurrencyName(currencyId);
        Cursor cursorSummary = new ExpenseRepo().selectTotalSpent(groupId);

        double sumAmountInBaseCurrency = 0;
        while (cursorSummary.moveToNext()) {
            int cId = cursorSummary.getInt(0);
            if (cId != currencyId) {
                int quantity = cursorSummary.getInt(2);
                int exchangeRate = cursorSummary.getInt(3);
                double amount = cursorSummary.getDouble(4);
                sumAmountInBaseCurrency += ( amount / quantity * exchangeRate );
            } else {
                sumAmountInBaseCurrency += cursorSummary.getDouble(4);
            }
            //sumAmountInBaseCurrency *= -1;
            Log.d(TAG, "initDataSummary: sum = " + sumAmountInBaseCurrency);
        }

        sumAmountInBaseCurrency = new BigDecimal(sumAmountInBaseCurrency)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        ExpenseSummary expenseSummary = new ExpenseSummary();
        expenseSummary.setCurrencyName(currencyName);
        expenseSummary.setSumSpent(sumAmountInBaseCurrency);

        return expenseSummary;
    }
}
