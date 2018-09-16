package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.ExpenseItem;
import cz.milandufek.dluzniceklite.models.SummaryExpense;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;
import cz.milandufek.dluzniceklite.utils.ExpenseRVAdapter;
import cz.milandufek.dluzniceklite.utils.MyNumbers;
import cz.milandufek.dluzniceklite.utils.MyPreferences;

public class ListExpenseFragment extends Fragment {
    private static final String TAG = "ListExpenseFragment";

    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_main_expenses, container, false);

        context = getActivity();

        setupSummaryView(view);

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
        int groupId = new MyPreferences(context).getActiveGroupId();
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
                debtors.append(selectTransactions.getString(0)); // member name
                sum += selectTransactions.getDouble(1); // amount
            }
            selectTransactions.close();

            sum = MyNumbers.roundIt(sum, 2);
            expenseItem.setDebtors(debtors.toString());
            expenseItem.setAmount(sum);

            expenseItems.add(expenseItem);
        }
        cursorExpenseItems.close();

        return expenseItems;
    }

    private void setupSummaryView(View view) {
        TextView summaryTitle = view.findViewById(R.id.tv_expense_summary_title);
        SummaryExpense summaryValues = new Summary().initSummaryExpense(context);
        String titleText = getString(R.string.total_spent);
        summaryTitle.setText(titleText);

        TextView summarySum = view.findViewById(R.id.tv_expense_summary_amount);
        StringBuilder sumSpent = new StringBuilder();
        sumSpent.append(summaryValues.getSumSpent());
        sumSpent.append(" ");
        sumSpent.append(summaryValues.getCurrencyName());
        summarySum.setText(sumSpent);
    }
}
