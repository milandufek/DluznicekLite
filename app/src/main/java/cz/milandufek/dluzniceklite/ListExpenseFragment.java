package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import cz.milandufek.dluzniceklite.models.SummaryExpenseItem;
import cz.milandufek.dluzniceklite.sql.ExpenseSql;
import cz.milandufek.dluzniceklite.adapters.ExpenseRVAdapter;
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

        int groupId = new MyPreferences(context).getActiveGroupId();

        setupSummaryView(view);

        RecyclerView recyclerView = view.findViewById(R.id.rv_expense_list);
        ExpenseRVAdapter adapter = new ExpenseRVAdapter(context, new ExpenseSql().getAllExpenseItems(groupId));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    private void setupSummaryView(View view) {
        TextView summaryTitle = view.findViewById(R.id.tv_expense_summary_title);
        SummaryExpenseItem summaryValues = new Summary().initSummaryExpense(context);
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
