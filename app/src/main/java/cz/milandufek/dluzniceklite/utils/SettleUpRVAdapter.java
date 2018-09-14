package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.milandufek.dluzniceklite.R;
import cz.milandufek.dluzniceklite.models.SettleUpTransaction;


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
        holder.amount.setText(String.valueOf(transactions.get(position).getAmount()) + " CZK");

        holder.parentLayout.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.parentLayout);
            popupMenu.inflate(R.menu.menu_item_settleup_onclick);
            popupMenu.setGravity(Gravity.END);
            popupMenu.setOnMenuItemClickListener(item -> {
                // TODO OnClick to settle-up the debt
                Toast.makeText(context, "TODO: vyrovnej dluh", Toast.LENGTH_SHORT).show();
                return true;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    /**
     *  Recycler View Holder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout parentLayout;
        TextView debtor, creditor, amount;

        private ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.item_settleup);
            debtor = itemView.findViewById(R.id.tv_settleup_debtor);
            creditor = itemView.findViewById(R.id.tv_settleup_creditor);
            amount = itemView.findViewById(R.id.tv_settleup_amount);
        }
    }
}
