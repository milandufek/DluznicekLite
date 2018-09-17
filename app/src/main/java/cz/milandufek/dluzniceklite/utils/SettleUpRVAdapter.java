package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.milandufek.dluzniceklite.R;
import cz.milandufek.dluzniceklite.models.Expense;
import cz.milandufek.dluzniceklite.models.SettleUpTransaction;
import cz.milandufek.dluzniceklite.models.Transaction;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;


public class SettleUpRVAdapter
        extends RecyclerView.Adapter<SettleUpRVAdapter.ViewHolder> {
    private static final String TAG = "SettleUpRVAdapter";

    private Context context;
    private List<SettleUpTransaction> transactions;

    public SettleUpRVAdapter(Context context, List<SettleUpTransaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_settleup, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.debtor.setText(transactions.get(position).getFrom());
        holder.creditor.setText(transactions.get(position).getTo());
        holder.amount.setText(String.valueOf(transactions.get(position).getAmount()));
        holder.currencyCode.setText(transactions.get(position).getCurrency().getName());

        holder.parentLayout.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.parentLayout);
            popupMenu.inflate(R.menu.menu_item_settleup_onclick);
            popupMenu.setGravity(Gravity.END);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_settleup_item:
                        onClickSettleUp(holder, position);
                        return true;
                    case R.id.action_settleup_part_item:
                        //return onClickSettleUpPartially(holder);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    private void onClickSettleUp(ViewHolder h, int position) {
        double amount = Double.parseDouble(h.amount.getText().toString());
        int senderId = transactions.get(position).getFromId();
        int receiverId = transactions.get(position).getToId();

        Log.d(TAG, "onClickSettleUp: senderId=" + senderId + " , receiverId=" + receiverId);

        MyPreferences preferences = new MyPreferences(context);
        int groupId = preferences.getActiveGroupId();
        int currencyId = preferences.getActiveGroupCurrencyId();

        if (settleUp(amount, senderId, receiverId, groupId, currencyId, true)) {
            removeItemFromList(h);
            Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickSettleUpPartially(ViewHolder holder) {

    }

    private boolean settleUp(double amount, int senderId, int receiverId, int groupId, int currencyId, boolean finalSettleUp) {
        Expense settleUpExpense = new Expense(0, senderId, groupId, currencyId,
                finalSettleUp ? context.getString(R.string.settleup_reason) : context.getString(R.string.settleup_part_reason),
                MyDateTime.getDateToday(),
                MyDateTime.getTimeNow());
        long settleUpExpenseId = new ExpenseRepo().insertExpense(settleUpExpense);

        Transaction transactionSender = new Transaction(0, senderId, (int) settleUpExpenseId, amount * -1, true);
        Transaction transactionReceiver = new Transaction(0, receiverId, (int) settleUpExpenseId, amount, true);

        TransactionRepo sql = new TransactionRepo();
        long idTransactionSender = sql.insertTransaction(transactionSender);
        long idTransactionReceiver = sql.insertTransaction(transactionReceiver);

        return idTransactionSender > -1 && idTransactionReceiver > -1;
    }

    private void removeItemFromList(ViewHolder h) {
        transactions.remove(h.getAdapterPosition());
        notifyItemRemoved(h.getAdapterPosition());
        notifyItemRangeChanged(h.getAdapterPosition(), getItemCount());
        notifyDataSetChanged();
    }

    /**
     *  Recycler View Holder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout parentLayout;
        TextView debtor, creditor, amount, currencyCode;

        private ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.item_settleup);
            debtor = itemView.findViewById(R.id.tv_settleup_debtor);
            creditor = itemView.findViewById(R.id.tv_settleup_creditor);
            amount = itemView.findViewById(R.id.tv_settleup_amount);
            currencyCode = itemView.findViewById(R.id.tv_settleup_currencycode);
        }
    }
}
