package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cz.milandufek.dluzniceklite.R;
import cz.milandufek.dluzniceklite.models.ExpenseItem;

public class ExpenseRVAdapter
        extends RecyclerView.Adapter<ExpenseRVAdapter.ViewHolder> {

    private static final String TAG = "ExpenseRecyclerViewAdap";

    private Context context;
    private List<ExpenseItem> expensesItems;

    public ExpenseRVAdapter(Context context, List<ExpenseItem> expensesItems) {
        this.context = context;
        this.expensesItems = expensesItems;
    }

    @NonNull
    @Override
    public ExpenseRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseRVAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        holder.expenseReason.setText(expensesItems.get(position).getReason());

        StringBuilder amountFormatted = new StringBuilder();
            amountFormatted.append(expensesItems.get(position).getAmount());
            amountFormatted.append(" ");
            amountFormatted.append(expensesItems.get(position).getCurrency());
        holder.expenseAmount.setText(amountFormatted);

        String payer = expensesItems.get(position).getPayer();
        String debtors = expensesItems.get(position).getDebtors();
        String date = expensesItems.get(position).getDate();
        String time = expensesItems.get(position).getTime();

        StringBuilder info = new StringBuilder();
            info.append(date);
            info.append(" ");
            info.append(time);
            info.append(" ");
            info.append(context.getString(R.string.big_dot));
            info.append(" ");
            info.append(payer);
            info.append(" ");
            info.append(context.getString(R.string.paid_for));
            info.append(" ");
            info.append(debtors);
            info.append(" ");
        holder.expenseInfo.setText(info);
        ;
    }

    @Override
    public int getItemCount() {
        return expensesItems.size();
    }

    /**
     * Recycler View holder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout itemExpense;
        TextView expenseReason, expenseAmount, expenseInfo;

        private ViewHolder(View itemView) {
            super(itemView);

            itemExpense = itemView.findViewById(R.id.item_expense);
            expenseReason = itemView.findViewById(R.id.tv_expense_item_reason);
            expenseAmount = itemView.findViewById(R.id.tv_expense_item_amount);
            expenseInfo   = itemView.findViewById(R.id.tv_expense_item_info);
        }
    }
}
