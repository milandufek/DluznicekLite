package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.milandufek.dluzniceklite.MainActivity;
import cz.milandufek.dluzniceklite.R;
import cz.milandufek.dluzniceklite.models.ExpenseItem;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        final int expenseId = expensesItems.get(position).getId();

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

        holder.itemExpense.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.itemExpense);
            popupMenu.inflate(R.menu.menu_item_onclick);
            popupMenu.setGravity(Gravity.END);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_edit_item:
                        onClickEdit(holder, expenseId);
                        Toast.makeText(context, "TODO: Edit item ",
                                Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.action_delete_item:
                        onClickDelete(holder, expenseId);
                        return true;

                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
    }

    private void onClickEdit(final ViewHolder h, final int id) {
        // TODO edit
    }

    private void onClickDelete(final ViewHolder h, final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.really_want_to_delete_expense)
                .setMessage(expensesItems.get(h.getAdapterPosition()).getReason())
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    ExpenseRepo repo = new ExpenseRepo();
                    repo.deleteExpense(id);
                    expensesItems.remove(h.getAdapterPosition());
                    notifyItemRemoved(h.getAdapterPosition());
                    notifyItemRangeChanged(h.getAdapterPosition(), expensesItems.size());
                    refreshActivity();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // TODO better to use MutableLiveData instead of
    private void refreshActivity() {
        Intent newActivity = new Intent(context, MainActivity.class);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(newActivity);
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
